package mypals.ml.transform;

import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class IntValueTransformer {
    private int last = 0;
    private int target = 0;
    private int current = 0;

    public IntValueTransformer(int init) {
        this.target = init;
        syncLastToTarget();
    }

    public IntValueTransformer() {
    }

    public void setTargetValue(int target) {
        this.target = target;
    }

    public void updateValue(Consumer<Integer> setter, float delta) {

        float lerped = MathHelper.lerp(delta, (float) last, (float) target);
        current = Math.round(lerped);
        setter.accept(current);
    }

    public void syncLastToTarget() {
        this.last = this.target;
    }

    public int getCurrentValue() {
        return current;
    }
}

