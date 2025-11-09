package mypals.ml.transform.shapeTransformers;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.shape.Shape;
import mypals.ml.transform.valueTransformers.QuaternionTransformer;
import mypals.ml.transform.valueTransformers.Vec3Transformer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class DefaultTransformer {
    private float delta = 0;
    public final Shape shape;

    public final TransformLayer local  = new TransformLayer();  // 模型本地
    public final TransformLayer world  = new TransformLayer();  // 世界空间
    public final TransformLayer matrix = new TransformLayer();  // 实时动画

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
        applyLayer(stack,local,false, lerp);
        applyLayer(stack,world,false,lerp);
        applyLayer(stack, matrix,false, lerp);
    }
    public void applyModelTransformations(PoseStack stack,boolean lerp) {
        applyLayer(stack,local,true,lerp);
        applyLayer(stack,world,false,lerp);
    }

    private void applyLayer(PoseStack stack, TransformLayer layer,boolean isLocal, boolean lerp) {
        Vec3 p = layer.position.getValue(lerp);
        Quaternionf r = layer.rotation.getValue(lerp);
        Vec3 s = layer.scale.getValue(lerp);

        if(isLocal){
            stack.translate(p.x, p.y, p.z);
            stack.mulPose(r);
            stack.scale((float) s.x, (float) s.y, (float) s.z);
            stack.translate(-p.x, -p.y, -p.z);
        }else{
            stack.translate(p.x, p.y, p.z);
            stack.mulPose(r);
            stack.scale((float) s.x, (float) s.y, (float) s.z);
        }
    }


    public TransformLayer local()  { return local; }
    public TransformLayer world()  { return world; }
    public TransformLayer matrix() { return matrix; }

    // ====================== WORLD GETTERS ======================
    public Vec3 getShapeWorldPivot(boolean lerp) {
        return world.getPosition(lerp);
    }

    public Quaternionf getShapeWorldRotation(boolean lerp) {
        return world.getRotation(lerp);
    }

    public Vec3 getShapeWorldScale(boolean lerp) {
        return world.getScale(lerp);
    }

    // ====================== LOCAL GETTERS ======================
    public Vec3 getShapeLocalPivot(boolean lerp) {
        return local.getPosition(lerp);
    }

    public Quaternionf getShapeLocalRotation(boolean lerp) {
        return local.getRotation(lerp);
    }

    public Vec3 getShapeLocalScale(boolean lerp) {
        return local.getScale(lerp);
    }

    // ====================== MATRIX (Render) GETTERS ======================
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


    // World
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

    // Local
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

    // Matrix (Render layer)
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