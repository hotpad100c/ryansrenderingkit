package mypals.ml.shape.basics;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.shape.Shape;
import mypals.ml.transform.Vec3dTransformer;
import net.minecraft.world.phys.Vec3;

public interface BoxLikeShape {
    Vec3 getMin();
    Vec3 getMax();
    void setMin(Vec3 min);
    void setMax(Vec3 max);
    void setDimensions(Vec3 dimensions);
    default Vec3 getShapeCenterPos() {
        double centerX = (getMin().x + getMax().x) / 2.0;
        double centerY = (getMin().y + getMax().y) / 2.0;
        double centerZ = (getMin().z + getMax().z) / 2.0;
        return new Vec3(centerX, centerY, centerZ);
    }
     class BoxTransformer extends Shape.DefaultTransformer {
        public BoxTransformer(Shape managedShape) {
            super(managedShape);
        }
        public Vec3dTransformer dimensionTransformer = new Vec3dTransformer();
        public void setDimension(Vec3 dimension) {
            this.dimensionTransformer.setTargetVector(dimension);
        }
        @Override
        public void applyTransformations(PoseStack matrixStack){
            super.applyTransformations(matrixStack);
            float deltaTime = getTickDelta();
            if(this.managedShape instanceof BoxLikeShape boxLikeShape) {
                dimensionTransformer.updateVector(boxLikeShape::setDimensions, deltaTime);
            }
        }
        public void syncLastToTarget(){
            this.dimensionTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }
    }
    default void normalizeBounds() {

    }
}
