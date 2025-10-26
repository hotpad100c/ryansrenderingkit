package mypals.ml.transform;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public class Vec3dTransformer {
    private Vec3d lastVector = Vec3d.ZERO;
    private Vec3d targetVector = Vec3d.ZERO;
    private Vec3d currentVector = Vec3d.ZERO;
    public Vec3dTransformer(Vec3d init){
        this.targetVector = init;
        syncLastToTarget();
    }
    public Vec3dTransformer(){

    }
    public void setTargetVector(Vec3d targetPosition) {
        this.targetVector = targetPosition;
    }
    public void updateVector(Consumer<Vec3d> setter, float delta) {
        double d = MathHelper.lerp((double)delta, lastVector.x, targetVector.x);
        double e = MathHelper.lerp((double)delta, lastVector.y, targetVector.y);
        double f = MathHelper.lerp((double)delta, lastVector.z, targetVector.z);
        currentVector = new Vec3d(d,e,f);
        setter.accept(currentVector);
    }
    public void syncLastToTarget() {
        this.lastVector = this.targetVector;
    }
    public Vec3d getCurrentVector(){
        return currentVector;
    }
}
