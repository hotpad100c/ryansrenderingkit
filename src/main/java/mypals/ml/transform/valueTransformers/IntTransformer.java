package mypals.ml.transform.valueTransformers;

import net.minecraft.util.Mth;

public final class IntTransformer extends ValueTransformer<Integer> {

    public IntTransformer(int initial) {
        this.target = initial;
        syncLastToTarget();
    }

    public IntTransformer() {
        this(0);
    }

    @Override
    public void updateVariables(float delta) {
        current = Math.round(Mth.lerp(delta, last.floatValue(), target.floatValue()));
    }

    @Override
    public void syncLastToTarget() {
        this.last = this.target;
        this.current = this.target;
    }

    @Override
    protected void setTarget(Integer value) {
        this.target = value;
    }

    public void setTargetValue(int v) {
        setTarget(v);
    }

    public int getCurrentValue() {
        return current;
    }
}

