package mypals.ml.transform;

import java.util.function.Consumer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Vec3dTransformer {
    private Vec3 lastVector = Vec3.ZERO;
    private Vec3 targetVector = Vec3.ZERO;
    private Vec3 currentVector = Vec3.ZERO;
    public Vec3dTransformer(Vec3 init){
        this.targetVector = init;
        syncLastToTarget();
    }
    public Vec3dTransformer(){

    }
    public void setTargetVector(Vec3 targetPosition) {
        this.targetVector = targetPosition;
    }
    public void updateVector(Consumer<Vec3> setter, float delta) {
        double d = Mth.lerp((double)delta, lastVector.x, targetVector.x);
        double e = Mth.lerp((double)delta, lastVector.y, targetVector.y);
        double f = Mth.lerp((double)delta, lastVector.z, targetVector.z);
        currentVector = new Vec3(d,e,f);
        setter.accept(currentVector);
    }
    public void syncLastToTarget() {
        this.lastVector = this.targetVector;
    }
    public Vec3 getCurrentVector(){
        return currentVector;
    }
}
