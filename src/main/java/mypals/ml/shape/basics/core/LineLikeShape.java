package mypals.ml.shape.basics.core;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableLine;
import mypals.ml.transform.FloatValueTransformer;
import net.minecraft.world.phys.Vec3;

public interface LineLikeShape extends DrawableLine {
    void setLineWidth(float width);
    default void addLineSegment(VertexBuilder vertexBuilder, Vec3 start, Vec3 end) {
        double dx = end.x() - start.x();
        double dy = end.y() - start.y();
        double dz = end.z() - start.z();

        double distanceInv = 1.0 / Math.sqrt(dx * dx + dy * dy + dz * dz);
        Vec3 normal = new Vec3(dx * distanceInv, dy * distanceInv, dz * distanceInv);

        vertexBuilder.putVertex(start, normal);
        vertexBuilder.putVertex(end, normal);
    }

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
        public void applyTransformations(PoseStack matrixStack){
            super.applyTransformations(matrixStack);
            float deltaTime = getTickDelta();
            if(this.managedShape instanceof LineLikeShape lineLikeShape) {
                this.widthTransformer.updateValue(lineLikeShape::setLineWidth,deltaTime);
            }
        }
    }

}
