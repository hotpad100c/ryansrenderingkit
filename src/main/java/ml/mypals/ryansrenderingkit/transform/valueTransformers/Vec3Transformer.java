package ml.mypals.ryansrenderingkit.transform.valueTransformers;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public final class Vec3Transformer extends ValueTransformer<Vector3d> {

    public Vec3Transformer(Vec3 initial) {
        this(new Vector3d(initial.x,initial.y,initial.z));
    }
    public Vec3Transformer(Vector3d initial) {
        this.target = initial;
        this.last = new Vector3d(initial);
        this.current = new Vector3d(initial);
        syncLastToTarget();
    }

    public Vec3Transformer() {
        this(Vec3.ZERO);
    }

    @Override
    public void updateVariables(float delta) {
        current.set(last.lerp(target, delta));
    }

    @Override
    public void syncLastToTarget() {
        this.last.set(this.target);
        this.current.set(this.target);
    }

    protected void setTarget(Vec3 value) {
        setTarget(new Vector3d(value.x, value.y, value.z));
    }

    @Override
    protected void setTarget(Vector3d value) {
        this.target = value;
    }

    public void setTargetVector(Vec3 v) {
        setTarget(v);
    }
}