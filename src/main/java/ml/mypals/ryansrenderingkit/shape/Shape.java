package ml.mypals.ryansrenderingkit.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import ml.mypals.ryansrenderingkit.collision.RayModelIntersection;
import ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
//? if >=1.21.9
/*import net.minecraft.client.gui.components.debug.DebugScreenEntries;*/
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.joml.Math;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.RENDER_PROFILER;
import static ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers.TEMP_HEADER;
import static ml.mypals.ryansrenderingkit.test.Debug.ENABLE_DEBUG;
import static ml.mypals.ryansrenderingkit.utils.Helpers.*;

public abstract class Shape {
    public enum RenderingType {IMMEDIATE, BATCH, BUFFERED}

    public boolean isTemp = false;
    public ResourceLocation id;
    public final RenderingType type;
    public DefaultTransformer transformer;
    public Consumer<DefaultTransformer> transformFunction;

    public Shape parent;
    public List<Shape> children = new ArrayList<>();

    public boolean enabled = true;
    public Color baseColor;
    public boolean seeThrough;

    public List<Vec3> model_vertexes = new ArrayList<>();//This is the original model of our model.
    public int[] indexBuffer = new int[0];
    public Map<String, Object> customData = new HashMap<>();

    protected Shape(RenderingType type, Consumer<DefaultTransformer> transform, Color color, Vec3 center, boolean seeThrough) {
        this(type, color, seeThrough);
        this.transformer = new DefaultTransformer(this, center);
        this.transformFunction = transform;
    }

    protected Shape(RenderingType type, Color color, boolean seeThrough) {
        this.type = type;
        this.seeThrough = seeThrough;
        this.baseColor = color;
    }

    public List<Shape> getChildren() {
        return this.children;
    }

    public void addChild(Shape shape) {
        shape.setParent(this);
        children.add(shape);
    }

    public void setParent(Shape parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        this.parent = parent;

    }

    public void setLocalPosition(Vec3 pos) {
        this.transformer.setShapeLocalPivot(pos);
    }

    public void setLocalRotation(Vector3f rot) {
        this.transformer.setShapeLocalRotationDegrees(rot.x(), rot.y(), rot.z());
    }

    public void setLocalScale(Vec3 scale) {
        this.transformer.setShapeLocalScale(scale);
    }

    public void setWorldPosition(Vec3 pos) {
        this.transformer.setShapeWorldPivot(pos);
    }

    public void setWorldRotation(Vector3f rot) {
        this.transformer.setShapeWorldRotationDegrees(rot.x(), rot.y(), rot.z());
    }

    public void setWorldScale(Vec3 scale) {
        this.transformer.setShapeWorldScale(scale);
    }


    public void setRenderPivot(Vec3 pos) {
        this.transformer.setShapeMatrixPivot(pos);
    }

    public void setRenderRotation(Vector3f rot) {
        this.transformer.setShapeMatrixRotationDegrees(rot.x(), rot.y(), rot.z());
    }

    public void setRenderScale(Vec3 scale) {
        this.transformer.setShapeMatrixScale(scale);
    }

    public void forceSetLocalPosition(Vec3 pos) {
        setLocalPosition(pos);
        this.transformer.local.position.syncLastToTarget();
    }

    public void forceSetLocalRotation(Vector3f rot) {
        setLocalRotation(rot);
        this.transformer.local.rotation.syncLastToTarget();
    }

    public void forceSetLocalScale(Vec3 scale) {
        setLocalScale(scale);
        this.transformer.local.scale.syncLastToTarget();
    }

    public void forceSetWorldPosition(Vec3 pos) {
        setWorldPosition(pos);
        this.transformer.world.position.syncLastToTarget();
    }

    public void forceSetWorldRotation(Vector3f rot) {
        setWorldRotation(rot);
        this.transformer.world.rotation.syncLastToTarget();
    }

    public void forceSetWorldScale(Vec3 scale) {
        setWorldScale(scale);
        this.transformer.world.scale.syncLastToTarget();
    }

    public void forceSetRenderPivot(Vec3 pos) {
        setRenderPivot(pos);
        this.transformer.matrix.position.syncLastToTarget();
    }

    public void forceSetRenderRotation(Vector3f rot) {
        setRenderRotation(rot);
        this.transformer.matrix.rotation.syncLastToTarget();
    }

    public void forceSetRenderScale(Vec3 scale) {
        setRenderScale(scale);
        this.transformer.matrix.scale.syncLastToTarget();
    }


    protected abstract void generateRawGeometry(boolean lerp);


    public List<Vec3> getModel(boolean applyMatrixTransformer) {
        if (this.transformer.asyncModelInfo()) {
            model_vertexes.clear();
            generateRawGeometry(false);
        }

        PoseStack poseStack = new PoseStack();

        List<Shape> hierarchy = getHierarchy();

        for (int i = hierarchy.size() - 1; i >= 0; i--) {
            Shape n = hierarchy.get(i);
            if (applyMatrixTransformer) {
                n.transformer.applyTransformations(poseStack, true);
            } else {
                n.transformer.applyModelTransformations(poseStack, true);
            }
        }

        Matrix4f matrix = convertToJomlIfNeeded(poseStack.last().pose());

        List<Vec3> transformed = new ArrayList<>(model_vertexes.size());
        for (Vec3 local : model_vertexes) {
            Vector3f vec = new Vector3f((float) local.x, (float) local.y, (float) local.z);
            //? if >1.18.2 {
            vec.mulPosition(matrix);
            //?} else {
            /*float x = vec.x(), y = vec.y(), z = vec.z();
            float newX = Math.fma(matrix.m00(), x, Math.fma(matrix.m10(), y, Math.fma(matrix.m20(), z, matrix.m30())));
            float newY = Math.fma(matrix.m01(), x, Math.fma(matrix.m11(), y, Math.fma(matrix.m21(), z, matrix.m31())));
            float newZ = Math.fma(matrix.m02(), x, Math.fma(matrix.m12(), y, Math.fma(matrix.m22(), z, matrix.m32())));
            vec = new Vector3f(newX, newY, newZ);
            *///?}
            transformed.add(new Vec3(vec.x(), vec.y(), vec.z()));
        }

        return transformed;
    }

    public List<Shape> getHierarchy() {
        Shape current = this;
        List<Shape> hierarchy = new ArrayList<>();
        while (current != null) {
            hierarchy.add(current);
            current = current.parent;
        }
        return hierarchy;
    }

    public void beforeDraw(PoseStack matrixStack, float deltaTime) {
        transformer.updateTickDelta(deltaTime);

        RENDER_PROFILER.push("applyCustomTransformer");
        transformFunction.accept(transformer);
        RENDER_PROFILER.pop();

        //RENDER_PROFILER.push("generateMesh");
        if (this.transformer.asyncModelInfo()) {
            model_vertexes.clear();
            generateRawGeometry(true);
        }
        //RENDER_PROFILER.pop();

        //RENDER_PROFILER.push("applyParentTransforms");
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
        //RENDER_PROFILER.pop();
    }

    public void drawShapeDebugInfo(PoseStack matrixStack, float deltaTime) {
        Entity entity = Minecraft.getInstance()./*? <1.21.9 {*/cameraEntity/*?} else {*//*getCameraEntity()*//*?}*/;
        if(entity == null)return;

        VertexConsumer vertexConsumer = Minecraft.getInstance()
                .renderBuffers().bufferSource().getBuffer(RenderType.LINES);

        Vec3 localCenter = this.transformer.getShapeWorldPivot(true).add(this.transformer.getShapeLocalPivot(true));
        Vec3 worldCenter = this.transformer.getShapeWorldPivot(true);
        Vec3 visualCenter = this.transformer.getShapeMatrixPivot(true).add(worldCenter);
        renderLineBox(matrixStack, vertexConsumer, localCenter, 0.15f, 1, 0, 0, 1);

        renderLineBox(matrixStack, vertexConsumer, worldCenter, 0.1f, 0, 1, 0, 1);

        renderLineBox(matrixStack, vertexConsumer, visualCenter, 0.05f, 0, 0, 1, 1);

        for (Vec3 v : getModel(false)) {
            double distanceTo = v.distanceToSqr(entity.position());
            if (distanceTo < 50)
                renderBillboardFrame(matrixStack, vertexConsumer, v, (float) (distanceTo * 0.01), 1, 0, 1, 1);
        }
    }

    public RayModelIntersection.HitResult isPlayerLookingAt() {
        Minecraft minecraft = Minecraft.getInstance();
        Player p = minecraft.player;
        if (p == null) return new RayModelIntersection.HitResult(false, null, -1);
        Camera camera = minecraft.gameRenderer.getMainCamera();
        RayModelIntersection.Ray r = new RayModelIntersection.Ray(camera.getPosition(), p.getForward());

        return RayModelIntersection.rayIntersectsModel(
                r,
                this.getModel(false),
                this.indexBuffer
        );
    }

    public void draw(boolean frustumCull, VertexBuilder builder, PoseStack matrixStack, float deltaTime) {

        if(!enabled) return;

        RENDER_PROFILER.push("pendingShouldDraw");
        boolean shouldDraw = enabled();
        RENDER_PROFILER.pop();
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        if (
            //? <1.21.9 {
            mc.getEntityRenderDispatcher().shouldRenderHitBoxes()
            //?} else {
            /*mc.debugEntries.isCurrentlyEnabled(DebugScreenEntries.ENTITY_HITBOXES)
            *///?}
            && ENABLE_DEBUG) {
            //RENDER_PROFILER.push("renderDebugInfo");
            drawShapeDebugInfo(matrixStack, deltaTime);
            //RENDER_PROFILER.pop();
        }

        matrixStack.pushPose();

        RENDER_PROFILER.push("setUpShapeForDraw");
        beforeDraw(matrixStack, deltaTime);
        builder.setPositionMatrix(convertToJomlIfNeeded(matrixStack.last().pose()));
        RENDER_PROFILER.pop();

        if (shouldDraw) {
            RENDER_PROFILER.push("drawShape");
            drawInternal(builder);
            RENDER_PROFILER.pop();
        }

        matrixStack.popPose();
        if (isTemp) discard();
    }

    public boolean enabled() {
        return enabled;
    }
    protected void drawInternal(VertexBuilder builder) {
        builder.putColor(baseColor);
        for (int i : indexBuffer) {
            builder.putVertex(model_vertexes.get(i));
        }
    }

    public void setBaseColor(Color color) {
        this.baseColor = color;
    }

    public Color getBaseColor() {return this.baseColor;}


    public void disable() {
        this.enabled = false;
    }

    public void enable() {this.enabled = true;}

    public void setId(ResourceLocation id) {
        this.id = id;
        this.isTemp = this.id.getPath().startsWith(TEMP_HEADER);
    }

    public void discard() {
        children.forEach(Shape::discard);
        ShapeManagers.removeShape(this.id);
    }

    public void syncLastToTarget() {
        transformer.syncLastToTarget();
    }

    private Map<String, Object> data() {
        if (customData == null) customData = new HashMap<>(1);
        return customData;
    }

    public <T> void putCustomData(String key, T value) {
        data().put(key, value);
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