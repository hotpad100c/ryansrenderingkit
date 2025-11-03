package mypals.ml.shape.basics.core;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.shape.Shape;
import mypals.ml.transform.Vec3dTransformer;
import net.minecraft.world.phys.Vec3;

public interface TwoPointsLineShape extends LineLikeShape{
    void setStart(Vec3 start);
    void setEnd(Vec3 end);
    Vec3 getStart();
    Vec3 getEnd();
    class TwoPointsLineTransformer extends DefaultLineTransformer {
        public TwoPointsLineTransformer(Shape managedShape) {
            super(managedShape);
        }
        public Vec3dTransformer startPosTransformer = new Vec3dTransformer();
        public Vec3dTransformer endPosTransformer = new Vec3dTransformer();
        public void setStartEnd(Vec3 start, Vec3 end) {
            this.endPosTransformer.setTargetVector(end);
            this.startPosTransformer.setTargetVector(start);
        }
        public void setStart(Vec3 start) {
            this.startPosTransformer.setTargetVector(start);
        }
        public void setEnd(Vec3 end) {
            this.endPosTransformer.setTargetVector(end);
        }
        @Override
        public void applyTransformations(PoseStack matrixStack){
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
