package mypals.ml.shape.basics;

import mypals.ml.shape.Shape;
import mypals.ml.transform.Vec3dTransformer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface BoxLikeShape {
    Vec3d getMin();
    Vec3d getMax();
    void setMin(Vec3d min);
    void setMax(Vec3d max);
    void setDimensions(Vec3d dimensions);
    default Vec3d getShapeCenterPos() {
        double centerX = (getMin().x + getMax().x) / 2.0;
        double centerY = (getMin().y + getMax().y) / 2.0;
        double centerZ = (getMin().z + getMax().z) / 2.0;
        return new Vec3d(centerX, centerY, centerZ);
    }
    public static class BoxTransformer extends Shape.DefaultTransformer {
        public BoxTransformer(Shape managedShape) {
            super(managedShape);
        }
        public Vec3dTransformer dimensionTransformer = new Vec3dTransformer();
        public void setDimension(Vec3d dimension) {
            this.dimensionTransformer.setTargetVector(dimension);
        }
        @Override
        public void applyTransformations(MatrixStack matrixStack){
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
