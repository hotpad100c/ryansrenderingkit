package mypals.ml.transform;

import java.util.function.Consumer;
import net.minecraft.util.Mth;

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

        float lerped = Mth.lerp(delta, (float) last, (float) target);
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

