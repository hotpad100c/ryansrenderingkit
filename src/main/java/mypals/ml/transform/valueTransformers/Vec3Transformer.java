package mypals.ml.transform.valueTransformers;

import net.minecraft.world.phys.Vec3;

public final class Vec3Transformer extends ValueTransformer<Vec3> {

    public Vec3Transformer(Vec3 initial) {
        this.target = initial;
        syncLastToTarget();
    }

    public Vec3Transformer() {
        this(Vec3.ZERO);
    }

    @Override
    public void updateVariables(float delta) {
        current = last.lerp(target, delta);
    }

    @Override
    public void syncLastToTarget() {
        this.last = this.target;
        this.current = this.target;
    }

    @Override
    protected void setTarget(Vec3 value) {
        this.target = value;
    }

    public void setTargetVector(Vec3 v) {
        setTarget(v);
    }
}