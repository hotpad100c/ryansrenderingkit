package mypals.ml.transform.valueTransformers;

import net.minecraft.util.Mth;

public final class FloatTransformer extends ValueTransformer<Float> {

    public FloatTransformer(float initial) {
        this.target = initial;
        syncLastToTarget();
    }

    public FloatTransformer() {
        this(0f);
    }

    @Override
    public void updateVariables(float delta) {
        current = Mth.lerp(delta, last, target);
    }

    @Override
    public void syncLastToTarget() {
        this.last = this.target;
        this.current = this.target;
    }

    @Override
    protected void setTarget(Float value) {
        this.target = value;
    }

    public void setTargetValue(float v) {
        setTarget(v);
    }

    public float getCurrentValue() {
        return current;
    }
}
