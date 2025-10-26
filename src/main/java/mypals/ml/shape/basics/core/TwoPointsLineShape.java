package mypals.ml.shape.basics.core;

import mypals.ml.shape.Shape;
import mypals.ml.transform.Vec3dTransformer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface TwoPointsLineShape extends LineLikeShape{
    void setStart(Vec3d start);
    void setEnd(Vec3d end);
    Vec3d getStart();
    Vec3d getEnd();
    class TwoPointsLineTransformer extends DefaultLineTransformer {
        public TwoPointsLineTransformer(Shape managedShape) {
            super(managedShape);
        }
        public Vec3dTransformer startPosTransformer = new Vec3dTransformer();
        public Vec3dTransformer endPosTransformer = new Vec3dTransformer();
        public void setStartEnd(Vec3d start, Vec3d end) {
            this.endPosTransformer.setTargetVector(end);
            this.startPosTransformer.setTargetVector(start);
        }
        public void setStart(Vec3d start) {
            this.startPosTransformer.setTargetVector(start);
        }
        public void setEnd(Vec3d end) {
            this.endPosTransformer.setTargetVector(end);
        }
        @Override
        public void applyTransformations(MatrixStack matrixStack){
            super.applyTransformations(matrixStack);
            float deltaTime = getTickDelta();
            if(this.managedShape instanceof TwoPointsLineShape twoPointLineShape) {
                startPosTransformer.updateVector(twoPointLineShape::setStart, deltaTime);
                endPosTransformer.updateVector(twoPointLineShape::setEnd, deltaTime);
            }
        }
        public void syncLastToTarget(){
            this.startPosTransformer.syncLastToTarget();
            this.endPosTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }
    }

}
