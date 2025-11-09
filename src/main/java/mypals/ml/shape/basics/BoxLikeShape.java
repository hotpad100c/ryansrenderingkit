package mypals.ml.shape.basics;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.shape.Shape;
import mypals.ml.shape.box.BoxShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.ModelInfoLayer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.BoxModelInfo;
import mypals.ml.transform.valueTransformers.Vec3Transformer;
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
     class BoxTransformer extends DefaultTransformer {

        public BoxModelInfo modelInfoLayer;
        public BoxTransformer(BoxShape managedShape,Vec3 dim,Vec3 center) {
            super(managedShape,center);
            modelInfoLayer = new BoxModelInfo(dim);
        }
        public void setDimension(Vec3 dimension) {
            this.modelInfoLayer.setDimension(dimension);
        }

        public void syncLastToTarget(){
            this.modelInfoLayer.syncLastToTarget();
            super.syncLastToTarget();
        }
        public Vec3 getDimension(boolean lerp){
            return modelInfoLayer.getDimension(lerp);
        }
         public boolean asyncModelInfo(){
             return modelInfoLayer.async();
         }
         @Override
         public void updateTickDelta(float delta){
            this.modelInfoLayer.update(delta);
            super.updateTickDelta(delta);
         }
    }
    default void normalizeBounds() {

    }
}
