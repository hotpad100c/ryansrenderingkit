package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.round.FaceCircleShape;
import ml.mypals.ryansserverrenderingkit.shape.round.SphereShape;

public class SphereBuilder extends BaseBuilder<SphereBuilder, FaceCircleShape.FaceCircleTransformer> {
    private int segments = 12;
    private float radius = 1.0f;

    public SphereBuilder segments(int segments) {
        this.segments = segments;
        return this;
    }

    public SphereBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public SphereShape build() {
        var t = getTransformer();
        SphereShape shape = new SphereShape(t, center, segments, radius, color, seeThrough);
        return applyCommon(shape);
    }
}