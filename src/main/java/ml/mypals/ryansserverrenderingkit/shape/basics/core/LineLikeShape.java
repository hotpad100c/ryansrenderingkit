package ml.mypals.ryansserverrenderingkit.shape.basics.core;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.DrawableLine;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import net.minecraft.world.phys.Vec3;

public interface LineLikeShape extends DrawableLine {
    void setLineWidth(float width);

    float getLineWidth(boolean lerp);

    class SimpleLineTransformer extends DefaultTransformer {
        public LineModelInfo lineModelInfo;

        public SimpleLineTransformer(Shape s, float width, Vec3 center) {
            super(s, center);
            lineModelInfo = new LineModelInfo(width);
        }

        public void setWidth(float width) {
            lineModelInfo.setWidth(width);
        }

        public float getWidth(boolean lerp) {
            return lineModelInfo.getWidth(lerp);
        }

        public void syncLastToTarget() {
            lineModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }

        public boolean asyncModelInfo() {
            return lineModelInfo.async();
        }

        @Override
        public void updateTickDelta(float delta) {
            this.lineModelInfo.update(delta);
            super.updateTickDelta(delta);
        }
    }
}
