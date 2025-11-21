package mypals.ml.transform.valueTransformers;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class Vec3Transformer extends ValueTransformer<Vec3> {

    public Vec3Transformer(Vec3 initial) {
        this.target = initial;
        syncLastToTarget();
    }

    public Vec3Transformer() { this(Vec3.ZERO); }

    @Override public void update(float delta) {
        double x = Mth.lerp(delta, last.x, target.x);
        double y = Mth.lerp(delta, last.y, target.y);
        double z = Mth.lerp(delta, last.z, target.z);
        current = new Vec3(x, y, z);
    }

    @Override public void syncLastToTarget() {
        this.last = this.target;
        this.current = this.target;
    }

    @Override protected void setTarget(Vec3 value) {
        this.target = value;
    }

    public void setTargetVector(Vec3 v) { setTarget(v); }
}