package ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer;

import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.ModelInfoLayer;
import ml.mypals.ryansserverrenderingkit.transform.valueTransformers.FloatTransformer;
import ml.mypals.ryansserverrenderingkit.transform.valueTransformers.IntTransformer;

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
