package mypals.ml.transform;

import java.util.function.Consumer;

import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class QuaternionTransformer {
    private Quaternionf lastRotation = new Quaternionf();
    private Quaternionf targetRotation = new Quaternionf();
    private Quaternionf currentRotation = new Quaternionf();
    public QuaternionTransformer(Quaternionf init){
        this.targetRotation = init;
        syncLastToTarget();
    }
    public QuaternionTransformer(){

    }
    public void setTargetRotation(Quaternionfc targetRotation) {
        this.targetRotation.set(targetRotation);
    }

    public void updateRotation(Consumer<Quaternionf> setter, float delta) {
        currentRotation = new Quaternionf();
        lastRotation.slerp(targetRotation, delta, currentRotation);

        setter.accept(currentRotation);

    }
    public void syncLastToTarget() {
        this.lastRotation.set(targetRotation);
    }
}
