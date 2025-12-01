package mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer;

import mypals.ml.transform.shapeTransformers.ModelInfoLayer;
import mypals.ml.transform.valueTransformers.FloatTransformer;
import mypals.ml.transform.valueTransformers.IntTransformer;

public class CircleModelInfo extends ModelInfoLayer {
    public IntTransformer segmentTransformer;
    public FloatTransformer radiusTransformer;

    public CircleModelInfo(int seg, float rad) {
        segmentTransformer = new IntTransformer(seg);
        radiusTransformer = new FloatTransformer(rad);
    }

    public boolean async() {
        return radiusTransformer.async() || segmentTransformer.async();
    }

    public void update(float delta) {
        radiusTransformer.update(delta);
        segmentTransformer.update(delta);
    }

    public float getRadius(boolean lerp) {
        return radiusTransformer.getValue(lerp);
    }

    public void setRadius(float target) {
        radiusTransformer.setTargetValue(target);
    }

    public int getSegment(boolean lerp) {
        return segmentTransformer.getValue(lerp);
    }

    public void setSegment(int target) {
        segmentTransformer.setTargetValue(target);
    }

    @Override
    public void syncLastToTarget() {
        this.segmentTransformer.syncLastToTarget();
        this.radiusTransformer.syncLastToTarget();
        super.syncLastToTarget();
    }
}
