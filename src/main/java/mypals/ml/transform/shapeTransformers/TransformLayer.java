package mypals.ml.transform.shapeTransformers;

import mypals.ml.transform.valueTransformers.QuaternionTransformer;
import mypals.ml.transform.valueTransformers.Vec3Transformer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class TransformLayer {
    public final Vec3Transformer position = new Vec3Transformer(Vec3.ZERO);
    public final QuaternionTransformer rotation = new QuaternionTransformer();
    public final Vec3Transformer scale = new Vec3Transformer(new Vec3(1,1,1));

    public void update(float delta) {
        position.update(delta);
        rotation.update(delta);
        scale.update(delta);
    }

    public void syncLastToTarget() {
        position.syncLastToTarget();
        rotation.syncLastToTarget();
        scale.syncLastToTarget();
    }

    public void setPosition(Vec3 v) {
        position.setTargetVector(v);
    }

    public void setRotation(Quaternionf q) {
        rotation.setTargetRotation(q);
    }

    public void setRotationDegrees(float x, float y, float z) {
        rotation.setTargetRotation(new Quaternionf().rotateXYZ(
                (float) Math.toRadians(x),
                (float) Math.toRadians(y),
                (float) Math.toRadians(z)
        ));
    }

    public void setScale(Vec3 s) {
        scale.setTargetVector(s);
    }

    public Vec3 getPosition(boolean useLerp) {
        Vector3d v = position.getValue(useLerp);
        return new Vec3(v.x, v.y, v.z);
    }

    public Vec3 getScale(boolean useLerp) {
        Vector3d v = scale.getValue(useLerp);
        return new Vec3(v.x, v.y, v.z);
    }

    public Quaternionf getRotation(boolean useLerp) {
        return rotation.getValue(useLerp);
    }
}