package mypals.ml.shape;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.collision.RayModelIntersection;
import mypals.ml.shapeManagers.ShapeManagers;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static mypals.ml.Helpers.*;

public abstract class Shape {
    public enum RenderingType { IMMEDIATE, BATCH, BUFFERED }

    public ResourceLocation id;
    public final RenderingType type;
    public DefaultTransformer transformer;
    public Consumer<DefaultTransformer> transformFunction;

    public Shape parent;
    public List<Shape> children = new ArrayList<>();

    public boolean visible = true;
    public Color baseColor = Color.WHITE;
    public boolean seeThrough = false;

    public List<Vec3> model_vertexes = new ArrayList<>();//This is the original model of our model.
    public int[] indexBuffer = new int[0];
    public Map<String, Object> customData = new HashMap<>();

    protected Shape(RenderingType type, Consumer<DefaultTransformer> transform,Color color, Vec3 center, boolean seeThrough) {
        this(type,color,seeThrough);
        this.transformer = new DefaultTransformer(this,center);
        this.transformFunction = transform;
    }
    protected Shape(RenderingType type,Color color, boolean seeThrough) {
        this.type = type;
        this.seeThrough = seeThrough;
        this.baseColor = color;
    }
    public List<Shape> getChildren(){
        return this.children;
    }
    public void addChild(Shape shape){
        shape.setParent(this);
        children.add(shape);
    }
    public void setParent(Shape parent){
        if(this.parent != null){
            this.parent.children.remove(this);
        }
        this.parent = parent;
    }
    public void setLocalPosition(Vec3 pos) { this.transformer.setShapeLocalPivot(pos); }
    public void setLocalRotation(Vector3f rot) { this.transformer.setShapeLocalRotationDegrees(rot.x,rot.y,rot.z); }
    public void setLocalScale(Vec3 scale) { this.transformer.setShapeLocalScale(scale); }

    public void setWorldPosition(Vec3 pos) {this.transformer.setShapeWorldPivot(pos);}

    public void setWorldRotation(Vector3f rot) {this.transformer.setShapeWorldRotationDegrees(rot.x, rot.y, rot.z);}

    public void setWorldScale(Vec3 scale) {this.transformer.setShapeWorldScale(scale);}

    public void setRenderPivot(Vec3 pos) {this.transformer.matrix.position.setTargetVector(pos);}

    public void setRenderRotation(Vector3f rot) {this.transformer.matrix.setRotationDegrees(rot.x, rot.y, rot.z);}

    public void setRenderScale(Vec3 scale) {this.transformer.matrix.scale.setTargetVector(scale);}

    protected abstract void generateRawGeometry(boolean lerp);


    public List<Vec3> getModel(boolean applyMatrixTransformer) {
        if (this.transformer.asyncModelInfo()) {
            model_vertexes.clear();
            generateRawGeometry(false);
        }

        PoseStack poseStack = new PoseStack();


        List<Shape> hierarchy = new ArrayList<>();
        Shape current = this;
        while (current != null) {
            hierarchy.add(current);
            current = current.parent;
        }
        for (int i = hierarchy.size() - 1; i >= 0; i--) {
            Shape n = hierarchy.get(i);
            if(applyMatrixTransformer){
                n.transformer.applyTransformations(poseStack, true);
            }else{
                n.transformer.applyModelTransformations(poseStack, true);
            }
        }

        Matrix4f matrix = poseStack.last().pose();

        List<Vec3> transformed = new ArrayList<>(model_vertexes.size());
        for (Vec3 local : model_vertexes) {
            Vector3f vec = new Vector3f((float) local.x, (float) local.y, (float) local.z);
            vec.mulPosition(matrix);
            transformed.add(new Vec3(vec.x(), vec.y(), vec.z()));
        }

        return transformed;
    }

    public void beforeDraw(PoseStack matrixStack, float deltaTime) {
        transformer.updateTickDelta(deltaTime);
        transformFunction.accept(transformer);
        if (this.transformer.asyncModelInfo()) {
            model_vertexes.clear();
            generateRawGeometry(true);
        }

        List<Shape> hierarchy = new ArrayList<>();
        Shape current = this;
        while (current != null) {
            hierarchy.add(current);
            current = current.parent;
        }
        for (int i = hierarchy.size() - 1; i >= 0; i--) {
            Shape n = hierarchy.get(i);
            n.transformer.applyTransformations(matrixStack, true);
        }
        //We'll apply all transformations to the matrix stack before drawing(With lerp!).
    }

    public RayModelIntersection.HitResult isPlayerLookingAt(){
        Minecraft minecraft = Minecraft.getInstance();
        Player p = minecraft.player;
        Camera camera = minecraft.gameRenderer.getMainCamera();
        RayModelIntersection.Ray r = new RayModelIntersection.Ray(camera.getPosition(), p.getForward());

        return RayModelIntersection.rayIntersectsModel(
                r,
                this.getModel(false),
                this.indexBuffer
        );
    }
    public void draw(boolean frustumCull, VertexBuilder builder, PoseStack matrixStack, float deltaTime) {
        if(!visible) return;
        matrixStack.pushPose();
        beforeDraw(matrixStack, deltaTime);
        builder.setPositionMatrix(matrixStack.last().pose());
        if(!frustumCull || shouldDraw()){
            drawInternal(builder);
        }
        matrixStack.popPose();
    }
    public boolean shouldDraw() {
        List<Vec3> vertices = this.getModel(true);
        if (vertices.isEmpty()) return false;

        Minecraft client = Minecraft.getInstance();
        Camera camera = client.gameRenderer.getMainCamera();
        GameRenderer gameRenderer = client.gameRenderer;

        Vec3 center = this.transformer.getWorldPivot().add(this.transformer.getLocalPivot());

        Matrix4f viewMatrix = createViewMatrix(camera);

        float fov = client.options.fov().get().floatValue();
        Matrix4f projectionMatrix = gameRenderer.getProjectionMatrix(fov);

        Matrix4f mvp = new Matrix4f(projectionMatrix);
        mvp.mul(viewMatrix);

        if (isVertexInFrustum(center, mvp)) return true;
        for (Vec3 v : vertices) {
            if (isVertexInFrustum(v, mvp)) return true;
        }
        return false;
    }

    public static boolean isVertexInFrustum(Vec3 v, Matrix4f mvp) {
        Vector4f clip = new Vector4f((float)v.x, (float)v.y, (float)v.z, 1f);
        clip.mul(mvp);

        if (clip.w <= 0) return false; // 在 near plane 之前

        float ndcX = clip.x / clip.w;
        float ndcY = clip.y / clip.w;
        float ndcZ = clip.z / clip.w;

        return ndcX >= -1 && ndcX <= 1
                && ndcY >= -1 && ndcY <= 1
                && ndcZ >= -1 && ndcZ <= 1; // 关键修复：Z 是 [-1,1]
    }
    protected void drawInternal(VertexBuilder builder) {
        builder.putColor(baseColor);
        for (int i : indexBuffer) {
            builder.putVertex(model_vertexes.get(i));
        }
    }
    public void setBaseColor(Color color){
            this.baseColor = color;
    }
    public void setId(ResourceLocation id) { this.id = id; }
    public void discard() {
        children.forEach(Shape::discard);
        ShapeManagers.removeShape(this.id); }
    public void syncLastToTarget() { transformer.syncLastToTarget(); }

    private Map<String, Object> data() {
        if (customData == null) customData = new HashMap<>(1);
        return customData;
    }

    public <T> void putCustomData(String key, T value) {
        data().put(key , value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getCustomData(String key, T def) {
        return (T) customData.getOrDefault(key, def);
    }
    @SuppressWarnings("unchecked")
    public void removeCustomData(String key) {
        customData.remove(key);
    }
}