package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.round.LineCircleShape;

public class LineCircleBuilder extends BaseBuilder<LineCircleBuilder, LineCircleShape.LineCircleTransformer> {
    private CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    private int segments = 16;
    private float radius = 1.0f;
    private float lineWidth = 0.05f;

    public LineCircleBuilder axis(CircleLikeShape.CircleAxis circleAxis) {
        this.circleAxis = circleAxis;
        return this;
    }

    public LineCircleBuilder segments(int segments) {
        this.segments = segments;
        return this;
    }

    public LineCircleBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    public LineCircleBuilder lineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    @Override
    public LineCircleShape build() {
        var t = getTransformer();
        LineCircleShape shape = new LineCircleShape(t, circleAxis, center, segments, radius, lineWidth, color, seeThrough);
        return applyCommon(shape);
    }
}
