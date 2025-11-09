package mypals.ml.shape.basics.core;

import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.shape.Shape;
import mypals.ml.shape.line.LineShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import mypals.ml.transform.valueTransformers.Vec3Transformer;
import net.minecraft.world.phys.Vec3;

public interface TwoPointsLineShape extends LineLikeShape{
    void setStart(Vec3 start);
    void setEnd(Vec3 end);
    Vec3 getStart(boolean lerp);
    Vec3 getEnd(boolean lerp);

    class TwoPointsLineTransformer extends DefaultTransformer {
        public LineModelInfo.TwoPointLineModelInfo lineModelInfo;
        public TwoPointsLineTransformer(LineShape managedShape,Vec3 start, Vec3 end,float width,Vec3 center) {
            super(managedShape,center);
            lineModelInfo = new LineModelInfo.TwoPointLineModelInfo(start,end,width);
        }
        public void setStartEnd(Vec3 start, Vec3 end) {
            this.lineModelInfo.setStart(start);
            this.lineModelInfo.setEnd(end);
        }
        public Vec3 getStart(boolean lerp){
            return lineModelInfo.getStart(lerp);
        }
        public Vec3 getEnd(boolean lerp){
            return lineModelInfo.getEnd(lerp);
        }
        public void setWidth(float width){
            lineModelInfo.setWidth(width);
        }
        public float getWidth(boolean lerp){
            return lineModelInfo.getWidth(lerp);
        }
        public void setStart(Vec3 start) {
            lineModelInfo.setStart(start);
        }
        public void setEnd(Vec3 end) {
            lineModelInfo.setEnd(end);
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
