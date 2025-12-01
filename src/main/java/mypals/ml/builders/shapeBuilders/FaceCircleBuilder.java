package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.round.FaceCircleShape;

public class FaceCircleBuilder extends BaseBuilder<FaceCircleBuilder, FaceCircleShape.FaceCircleTransformer> {
    private CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    private int segments = 32;
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
    public FaceCircleShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new FaceCircleShape(type, t, circleAxis, center, segments, radius, color, seeThrough);
    }
}