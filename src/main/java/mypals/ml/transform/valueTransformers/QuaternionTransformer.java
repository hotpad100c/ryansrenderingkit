package mypals.ml.transform.valueTransformers;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class QuaternionTransformer extends ValueTransformer<Quaternionf> {

    public QuaternionTransformer(Quaternionf initial) {
        this.target = new Quaternionf(initial);
        syncLastToTarget();
    }

    public QuaternionTransformer() {
        this(new Quaternionf());
    }


    @Override
    public void updateVariables(float delta) {
        last.slerp(target, delta, current);
    }

    @Override
    public void syncLastToTarget() {
        this.last = new Quaternionf(target);
        this.current = new Quaternionf(target);
    }

    @Override
    protected void setTarget(Quaternionf value) {
        this.target.set(value);
    }

    public void setTargetRotation(Quaternionfc rot) {
        setTarget(new Quaternionf(rot));
    }
}
