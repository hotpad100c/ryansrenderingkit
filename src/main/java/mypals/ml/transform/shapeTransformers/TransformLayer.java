package mypals.ml.transform.shapeTransformers;

import mypals.ml.transform.valueTransformers.QuaternionTransformer;
import mypals.ml.transform.valueTransformers.Vec3Transformer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public  class TransformLayer {
    public final Vec3Transformer position = new Vec3Transformer(Vec3.ZERO);
    public final QuaternionTransformer rotation = new QuaternionTransformer();
    public final Vec3Transformer scale = new Vec3Transformer(Vec3.ZERO.add(1));

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

    public void setPosition(Vec3 v) { position.setTargetVector(v); }
    public void setRotation(Quaternionf q) { rotation.setTargetRotation(q); }
    public void setRotationDegrees(float x, float y, float z) {
        rotation.setTargetRotation(new Quaternionf().rotateXYZ(
                (float)Math.toRadians(x),
                (float)Math.toRadians(y),
                (float)Math.toRadians(z)
        ));
    }
    public void setScale(Vec3 s) { scale.setTargetVector(s); }
    public Vec3 getPosition(boolean useLerp) { return position.getValue(useLerp); }
    public Vec3 getScale(boolean useLerp) { return scale.getValue(useLerp); }
    public Quaternionf getRotation(boolean useLerp) { return rotation.getValue(useLerp); }
}