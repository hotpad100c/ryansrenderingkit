package mypals.ml.shape.basics.core;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.drawTypes.DrawableLine;
import mypals.ml.transform.FloatValueTransformer;
import net.minecraft.client.util.math.MatrixStack;

public interface LineLikeShape extends DrawableLine {
    void setLineWidth(float width);


    class DefaultLineTransformer extends Shape.DefaultTransformer{
        public FloatValueTransformer widthTransformer = new FloatValueTransformer();
        public DefaultLineTransformer(Shape managerShape) {
            super(managerShape);
        }
        public void syncLastToTarget(){
            this.widthTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }
        public void setWidth(float width){
            this.widthTransformer.setTargetValue(width);
        }
        @Override
        public void applyTransformations(MatrixStack matrixStack){
            super.applyTransformations(matrixStack);
            float deltaTime = getTickDelta();
            if(this.managedShape instanceof LineLikeShape lineLikeShape) {
                this.widthTransformer.updateValue(lineLikeShape::setLineWidth,deltaTime);
            }
        }
    }

}
