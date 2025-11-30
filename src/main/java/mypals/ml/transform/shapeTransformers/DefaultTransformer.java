package mypals.ml.transform.shapeTransformers;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.shape.Shape;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.List;

public class DefaultTransformer {
    private float delta = 0;
    public final Shape shape;

    public final TransformLayer local  = new TransformLayer();
    public final TransformLayer world  = new TransformLayer();
    public final TransformLayer matrix = new TransformLayer();

    public DefaultTransformer(Shape s,Vec3 center) {
        this.shape = s;
        this.world.setPosition(center);
        this.world.syncLastToTarget();
    }

    public Shape getShape() {
        return shape;
    }

    public void updateTickDelta(float d) {
        this.delta = d;
        updateAll(d);
    }
    public float getTickDelta() { return delta; }
    public void updateAll(float t) {
        local.update(t);
        world.update(t);
        matrix.update(t);
    }

    public void syncLastToTarget() {
        local.syncLastToTarget();
        world.syncLastToTarget();
        matrix.syncLastToTarget();
    }

    public void applyTransformations(PoseStack stack,boolean lerp) {
        applyLayer(stack,world,lerp);
        applyLayer(stack,local, lerp);
        applyLayer(stack, matrix, lerp);
    }
    public void applyModelTransformations(PoseStack stack,boolean lerp) {
        applyLayer(stack,world,lerp);
        applyLayer(stack,local,lerp);
    }

    private void applyLayer(PoseStack stack, TransformLayer layer, boolean lerp) {
        Vec3 p = layer.position.getValue(lerp);
        Quaternionf r = layer.rotation.getValue(lerp);
        Vec3 s = layer.scale.getValue(lerp);

        stack.translate(p.x, p.y, p.z);
        stack.mulPose(r);
        stack.scale((float) s.x, (float) s.y, (float) s.z);
    }


    public TransformLayer local()  { return local; }
    public TransformLayer world()  { return world; }
    public TransformLayer matrix() { return matrix; }

    public Vec3 getShapeWorldPivot(boolean lerp) {
        PoseStack poseStack = new PoseStack();

        List<Shape> hierarchy = this.shape.getHierarchy();
        for (int i = hierarchy.size() - 1; i >= 1; i--) {
            Shape n = hierarchy.get(i);
            if (n.transformer != null) {
                n.transformer.applyTransformations(poseStack, true);
            }
        }

        Vec3 localPivot = this.world.getPosition(lerp);

        Matrix4f mat = poseStack.last().pose();

        Vector4f v = new Vector4f(
                (float) localPivot.x,
                (float) localPivot.y,
                (float) localPivot.z,
                1.0f
        );

        v.mul(mat);

        return new Vec3(v.x(), v.y(), v.z());
    }


    public Quaternionf getShapeWorldRotation(boolean lerp) {
        return world.getRotation(lerp);
    }

    public Vec3 getShapeWorldScale(boolean lerp) {
        return world.getScale(lerp);
    }

    public Vec3 getShapeLocalPivot(boolean lerp) {
        return local.getPosition(lerp);
    }

    public Quaternionf getShapeLocalRotation(boolean lerp) {
        return local.getRotation(lerp);
    }

    public Vec3 getShapeLocalScale(boolean lerp) {
        return local.getScale(lerp);
    }

    public Vec3 getShapeMatrixPivot(boolean lerp) {
        return matrix.getPosition(lerp);
    }

    public Quaternionf getShapeMatrixRotation(boolean lerp) {
        return matrix.getRotation(lerp);
    }

    public Vec3 getShapeMatrixScale(boolean lerp) {
        return matrix.getScale(lerp);
    }

    public Vec3 getWorldPivot() { return getShapeWorldPivot(true); }
    public Quaternionf getWorldRotation() { return getShapeWorldRotation(true); }
    public Vec3 getWorldScale() { return getShapeWorldScale(true); }

    public Vec3 getLocalPivot() { return getShapeLocalPivot(true); }
    public Quaternionf getLocalRotation() { return getShapeLocalRotation(true); }
    public Vec3 getLocalScale() { return getShapeLocalScale(true); }

    public Vec3 getMatrixPivot() { return getShapeMatrixPivot(true); }
    public Quaternionf getMatrixRotation() { return getShapeMatrixRotation(true); }
    public Vec3 getMatrixScale() { return getShapeMatrixScale(true); }


    public void setShapeWorldPivot(Vec3 v)               { world.setPosition(v); }
    public void setShapeWorldRotation(Quaternionf q)     { world.setRotation(q); }
    public void setShapeWorldRotationDegrees(float x, float y, float z) {
        world.setRotation(new Quaternionf().rotateXYZ(
                (float) Math.toRadians(x),
                (float) Math.toRadians(y),
                (float) Math.toRadians(z)
        ));
    }
    public void setShapeWorldScale(Vec3 s)               { world.setScale(s); }

    public void setShapeLocalPivot(Vec3 v)               { local.setPosition(v); }
    public void setShapeLocalRotation(Quaternionf q)     { local.setRotation(q); }
    public void setShapeLocalRotationDegrees(float x, float y, float z) {
        local.setRotation(new Quaternionf().rotateXYZ(
                (float) Math.toRadians(x),
                (float) Math.toRadians(y),
                (float) Math.toRadians(z)
        ));
    }
    public void setShapeLocalScale(Vec3 s)               { local.setScale(s); }

    public void setShapeMatrixPivot(Vec3 v)              { matrix.setPosition(v); }
    public void setShapeMatrixRotation(Quaternionf q)    { matrix.setRotation(q); }
    public void setShapeMatrixRotationDegrees(float x, float y, float z) {
        matrix.setRotation(new Quaternionf().rotateXYZ(
                (float) Math.toRadians(x),
                (float) Math.toRadians(y),
                (float) Math.toRadians(z)
        ));
    }
    public void setShapeMatrixScale(Vec3 s)              { matrix.setScale(s); }
    public boolean asyncModelInfo(){
        return false;
    }
}