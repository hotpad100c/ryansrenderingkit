package mypals.ml.transform;

import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class FloatValueTransformer {
    private float last = 0;
    private float target = 0;
    private float current = 0;
    public FloatValueTransformer(float init){
        this.target = init;
        syncLastToTarget();
    }
    public FloatValueTransformer(){

    }
    public void setTargetValue(float target) {
        this.target = target;
    }
    public void updateValue(Consumer<Float> setter, float delta) {
        current = (float) MathHelper.lerp((double)delta, last, target);
        setter.accept(current);
    }
    public void syncLastToTarget() {
        this.last = this.target;
    }
    public float getCurrentValue(){
        return current;
    }
}
