package mypals.ml.shape.basics.core;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableLine;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import net.minecraft.world.phys.Vec3;

public interface LineLikeShape extends DrawableLine {
    void setLineWidth(float width);
    float getLineWidth(boolean lerp);
    default void addLineSegment(VertexBuilder vertexBuilder, Vec3 start, Vec3 end) {
        double dx = end.x() - start.x();
        double dy = end.y() - start.y();
        double dz = end.z() - start.z();

        double distanceInv = 1.0 / Math.sqrt(dx * dx + dy * dy + dz * dz);
        Vec3 normal = new Vec3(dx * distanceInv, dy * distanceInv, dz * distanceInv);

        vertexBuilder.putVertex(start, normal);
        vertexBuilder.putVertex(end, normal);
    }
    class SimpleLineTransformer extends DefaultTransformer {
        public LineModelInfo lineModelInfo;
        public SimpleLineTransformer(Shape s, float width, Vec3 center) {
            super(s, center);
            lineModelInfo = new LineModelInfo(width);
        }
        public void setWidth(float width){
            lineModelInfo.setWidth(width);
        }
        public float getWidth(boolean lerp){
            return lineModelInfo.getWidth(lerp);
        }
        public void syncLastToTarget(){
            lineModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }
        public boolean asyncModelInfo(){
            return lineModelInfo.async();
        }
        @Override
        public void updateTickDelta(float delta){
            this.lineModelInfo.update(delta);
            super.updateTickDelta(delta);
        }
    }
}
