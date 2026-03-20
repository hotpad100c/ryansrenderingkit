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
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.rendertype.RenderType;
//? if >=1.21.11 {
import net.minecraft.gizmos.Gizmos;
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?}
import net.minecraft.resources.Identifier;
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
import static ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer.*;
import static ml.mypals.ryansrenderingkit.utils.Helpers.*;
public abstract class Shape {
    public enum RenderingType {IMMEDIATE, BATCH, BUFFERED}

    public boolean isTemp = false;
    public Identifier id;
    public final RenderingType type;
    public DefaultTransformer transformer;
    public Consumer<DefaultTransformer> transformFunction;

    public Shape parent;
    public List<Shape> children = new ArrayList<>();

    public boolean enabled = true;
    public Color baseColor;
    public boolean seeThrough;

    public List<Vec3> model_vertexes = new ArrayList<>();
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

    public void setLocalPosition(Vec3 pos)          { transformer.setShapeLocalPivot(pos); }
    public void setLocalRotation(Vector3f rot)       { transformer.setShapeLocalRotationDegrees(rot.x(), rot.y(), rot.z()); }
    public void setLocalScale(Vec3 scale)            { transformer.setShapeLocalScale(scale); }
    public void setWorldPosition(Vec3 pos)           { transformer.setShapeWorldPivot(pos); }
    public void setWorldRotation(Vector3f rot)       { transformer.setShapeWorldRotationDegrees(rot.x(), rot.y(), rot.z()); }
    public void setWorldScale(Vec3 scale)            { transformer.setShapeWorldScale(scale); }
    public void setRenderPivot(Vec3 pos)             { transformer.setShapeMatrixPivot(pos); }
    public void setRenderRotation(Vector3f rot)      { transformer.setShapeMatrixRotationDegrees(rot.x(), rot.y(), rot.z()); }
    public void setRenderScale(Vec3 scale)           { transformer.setShapeMatrixScale(scale); }

    public void forceSetLocalPosition(Vec3 pos)     { setLocalPosition(pos);   transformer.local.position.syncLastToTarget(); }
    public void forceSetLocalRotation(Vector3f rot)  { setLocalRotation(rot);   transformer.local.rotation.syncLastToTarget(); }
    public void forceSetLocalScale(Vec3 scale)       { setLocalScale(scale);    transformer.local.scale.syncLastToTarget(); }
    public void forceSetWorldPosition(Vec3 pos)      { setWorldPosition(pos);   transformer.world.position.syncLastToTarget(); }
    public void forceSetWorldRotation(Vector3f rot)  { setWorldRotation(rot);   transformer.world.rotation.syncLastToTarget(); }
    public void forceSetWorldScale(Vec3 scale)       { setWorldScale(scale);    transformer.world.scale.syncLastToTarget(); }
    public void forceSetRenderPivot(Vec3 pos)        { setRenderPivot(pos);     transformer.matrix.position.syncLastToTarget(); }
    public void forceSetRenderRotation(Vector3f rot) { setRenderRotation(rot);  transformer.matrix.rotation.syncLastToTarget(); }
    public void forceSetRenderScale(Vec3 scale)      { setRenderScale(scale);   transformer.matrix.scale.syncLastToTarget(); }

    protected abstract void generateRawGeometry(boolean lerp);
    private void refreshGeometryIfNeeded(boolean lerp) {
        if (transformer.asyncModelInfo()) {
            model_vertexes.clear();
            generateRawGeometry(lerp);
        }
    }
    private void applyHierarchyTransforms(PoseStack poseStack, int flags) {
        List<Shape> hierarchy = getHierarchy();
        for (int i = hierarchy.size() - 1; i >= 0; i--) {
            hierarchy.get(i).transformer.applyTransformations(poseStack, true, flags);
        }
    }

    public List<Vec3> getModel(boolean applyMatrixTransformer) {
        return getModel(applyMatrixTransformer, false);
    }

    public List<Vec3> getModel(boolean applyMatrixTransformer, boolean camSpace) {
        refreshGeometryIfNeeded(false);

        PoseStack poseStack = new PoseStack();
        int flags = WORLD | LOCAL | (camSpace ? CAMSPACE : 0) | (applyMatrixTransformer ? MATRIX : 0);
        applyHierarchyTransforms(poseStack, flags);

        Matrix4f matrix = convertToJomlIfNeeded(poseStack.last().pose());
        List<Vec3> transformed = new ArrayList<>(model_vertexes.size());
        Vector3f vec = new Vector3f();

        for (Vec3 local : model_vertexes) {
            vec.set((float) local.x, (float) local.y, (float) local.z);
            //? if >1.18.2 {
            vec.mulPosition(matrix);
            //?} else {
            /*float x = vec.x(), y = vec.y(), z = vec.z();
            float newX = Math.fma(matrix.m00(), x, Math.fma(matrix.m10(), y, Math.fma(matrix.m20(), z, matrix.m30())));
            float newY = Math.fma(matrix.m01(), x, Math.fma(matrix.m11(), y, Math.fma(matrix.m21(), z, matrix.m31())));
            float newZ = Math.fma(matrix.m02(), x, Math.fma(matrix.m12(), y, Math.fma(matrix.m22(), z, matrix.m32())));
            vec.set(newX, newY, newZ);
            *///?}
            transformed.add(new Vec3(vec.x(), vec.y(), vec.z()));
        }

        return transformed;
    }

    public List<Shape> getHierarchy() {
        List<Shape> hierarchy = new ArrayList<>();
        Shape current = this;
        while (current != null) {
            hierarchy.add(current);
            current = current.parent;
        }
        return hierarchy;
    }

    public void beforeDraw(PoseStack matrixStack, float deltaTime) {
        beforeDraw(matrixStack, deltaTime, true);
    }

    public void beforeDraw(PoseStack matrixStack, float deltaTime, boolean camSpace) {
        transformer.updateTickDelta(deltaTime);

        RENDER_PROFILER.push("applyCustomTransformer");
        transformFunction.accept(transformer);
        RENDER_PROFILER.pop();

        refreshGeometryIfNeeded(true);

        applyHierarchyTransforms(matrixStack, WORLD | LOCAL | MATRIX | (camSpace ? CAMSPACE : 0));
    }

    public void drawShapeDebugInfo(PoseStack matrixStack, float deltaTime) {
        VertexConsumer vertexConsumer = Minecraft.getInstance()
                .renderBuffers().bufferSource().getBuffer(/*? if <1.21.11 {*//*RenderType.*//*?} else {*/RenderTypes. /*?}*/LINES);

        matrixStack.pushPose();
        transformer.applyLayer(matrixStack, transformer.world, true, true);
        renderLineBox(matrixStack, vertexConsumer, Vec3.ZERO, 0.15f, 1, 0, 0, 1);
        transformer.applyLayer(matrixStack, transformer.local, true, false);
        renderLineBox(matrixStack, vertexConsumer, Vec3.ZERO, 0.1f, 0, 1, 0, 1);
        transformer.applyLayer(matrixStack, transformer.matrix, true, false);
        renderLineBox(matrixStack, vertexConsumer, Vec3.ZERO, 0.05f, 0, 0, 1, 1);
        matrixStack.popPose();

        for (Vec3 v : getModel(false, true)) {
            double distanceTo = v.distanceTo(Vec3.ZERO);
            if (distanceTo < 30)
                //? if >=1.21.11 {
                Gizmos.point(v, Color.MAGENTA.getRGB(), 10);
                 //?} else {
                /*renderBillboardFrame(matrixStack, vertexConsumer, v, (float) (distanceTo * 0.03), 1, 0, 1, 1);
            *///?}
        }
    }

    public RayModelIntersection.HitResult isPlayerLookingAt() {
        Minecraft minecraft = Minecraft.getInstance();
        Player p = minecraft.player;
        if (p == null) return new RayModelIntersection.HitResult(false, null, -1);

        Entity entity = minecraft./*? <1.21.9 {*//*cameraEntity*//*?} else {*/getCameraEntity()/*?}*/;
        if (entity == null) return null;

        Camera camera = minecraft.gameRenderer.getMainCamera();
        RayModelIntersection.Ray r = new RayModelIntersection.Ray(
                camera./*? if >=1.21.11 {*/position()/*?} else {*//*getPosition()*//*?}*/,
                p.getForward()
        );

        return RayModelIntersection.rayIntersectsModel(r, getModel(false), this.indexBuffer);
    }

    public void draw(boolean inCamSpace, VertexBuilder builder, PoseStack matrixStack, float deltaTime) {
        if (!enabled) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        RENDER_PROFILER.push("pendingShouldDraw");
        boolean shouldDraw = enabled();
        RENDER_PROFILER.pop();

        if (
            //? <1.21.9 {
                /*mc.getEntityRenderDispatcher().shouldRenderHitBoxes()
                        *///?} else {
                        mc.debugEntries.isCurrentlyEnabled(DebugScreenEntries.ENTITY_HITBOXES)
                         //?}
                        && ENABLE_DEBUG) {
            drawShapeDebugInfo(matrixStack, deltaTime);
        }

        matrixStack.pushPose();

        RENDER_PROFILER.push("setUpShapeForDraw");
        beforeDraw(matrixStack, deltaTime, inCamSpace);
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

    public void setBaseColor(Color color) { this.baseColor = color; }
    public Color getBaseColor() { return this.baseColor; }
    public void disable() { this.enabled = false; }
    public void enable() { this.enabled = true; }

    public void setId(Identifier id) {
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

    public <T> void putCustomData(String key, T value) {
        customData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getCustomData(String key, T def) {
        return (T) customData.getOrDefault(key, def);
    }

    public void removeCustomData(String key) {
        customData.remove(key);
    }
}