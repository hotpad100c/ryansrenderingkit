package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.round.FaceCircleShape;

public class FaceCircleBuilder extends BaseBuilder<FaceCircleBuilder, FaceCircleShape.FaceCircleTransformer> {
    private CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    private int segments = 16;
    private float radius = 1.0f;

    public FaceCircleBuilder axis(CircleLikeShape.CircleAxis circleAxis) {
        this.circleAxis = circleAxis;
        return this;
    }

    public FaceCircleBuilder segments(int segments) {
        this.segments = segments;
        return this;
    }

    public FaceCircleBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public FaceCircleShape build() {
        var t = getTransformer();
        FaceCircleShape shape = new FaceCircleShape(t, circleAxis, center, segments, radius, color, seeThrough);
        return applyCommon(shape);
    }
}