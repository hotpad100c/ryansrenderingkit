package mypals.ml.shape.basics;

import mypals.ml.shape.box.BoxShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.BoxModelInfo;
import net.minecraft.world.phys.Vec3;

public interface BoxLikeShape {
    Vec3 getMin();

    Vec3 getMax();

    void setMin(Vec3 min);

    void setMax(Vec3 max);

    void setDimension(Vec3 dimensions);

    default Vec3 getShapeCenterPos() {
        double centerX = (getMin().x + getMax().x) / 2.0;
        double centerY = (getMin().y + getMax().y) / 2.0;
        double centerZ = (getMin().z + getMax().z) / 2.0;
        return new Vec3(centerX, centerY, centerZ);
    }

    class BoxTransformer extends DefaultTransformer {

        public BoxModelInfo boxModelInfo;

        public BoxTransformer(BoxShape managedShape, Vec3 dim, Vec3 center) {
            super(managedShape, center);
            boxModelInfo = new BoxModelInfo(dim);
        }

        public void setDimension(Vec3 dimension) {
            this.boxModelInfo.setDimension(dimension);
        }

        public void syncLastToTarget() {
            this.boxModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }

        public Vec3 getDimension(boolean lerp) {
            return boxModelInfo.getDimension(lerp);
        }

        public boolean asyncModelInfo() {
            return boxModelInfo.async();
        }

        @Override
        public void updateTickDelta(float delta) {
            this.boxModelInfo.update(delta);
            super.updateTickDelta(delta);
        }
    }

    default void normalizeBounds() {

    }
}
