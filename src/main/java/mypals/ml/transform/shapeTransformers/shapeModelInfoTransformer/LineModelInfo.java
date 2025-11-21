package mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer;

import mypals.ml.transform.shapeTransformers.ModelInfoLayer;
import mypals.ml.transform.valueTransformers.FloatTransformer;
import mypals.ml.transform.valueTransformers.Vec3Transformer;
import net.minecraft.world.phys.Vec3;

public class LineModelInfo extends ModelInfoLayer {
    public FloatTransformer widthTransformer;
    public LineModelInfo(float width){
        this.widthTransformer = new FloatTransformer(width);
    }
    public boolean async(){
        return widthTransformer.async();
    }
    public void update(float delta){
        widthTransformer.update(delta);
    }
    public float getWidth(boolean lerp){
        return widthTransformer.getValue(lerp);
    }
    public void setWidth(float target){
        widthTransformer.setTargetValue(target);
    }

    @Override
    public void syncLastToTarget() {
        this.widthTransformer.syncLastToTarget();
        super.syncLastToTarget();
    }
    public static class TwoPointLineModelInfo extends LineModelInfo {
        public Vec3Transformer endPointTransformer;
        public Vec3Transformer startPointTransformer;
        public TwoPointLineModelInfo(Vec3 start, Vec3 end,float width){
            super(width);
            endPointTransformer = new Vec3Transformer(end);
            startPointTransformer = new Vec3Transformer(start);
        }
        public boolean async(){
            return super.async() || endPointTransformer.async() || startPointTransformer.async();
        }
        public void update(float delta){
            endPointTransformer.update(delta);
            startPointTransformer.update(delta);
            super.update(delta);
        }
        public Vec3 getStart(boolean lerp){
            return startPointTransformer.getValue(lerp);
        }
        public Vec3 getEnd(boolean lerp){
            return endPointTransformer.getValue(lerp);
        }
        public void setStart(Vec3 target){
            startPointTransformer.setTargetVector(target);
        }
        public void setEnd(Vec3 target){
            endPointTransformer.setTargetVector(target);
        }

        @Override
        public void syncLastToTarget() {
            super.syncLastToTarget();
            this.startPointTransformer.syncLastToTarget();
            this.endPointTransformer.syncLastToTarget();
        }
    }
}