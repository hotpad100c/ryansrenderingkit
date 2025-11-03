package mypals.ml.builders.shapeBuilders;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.round.FaceCircleShape;
import mypals.ml.shape.round.LineCircleShape;

import java.util.function.BiConsumer;
public class LineCircleBuilder extends BaseBuilder<LineCircleBuilder, LineCircleShape.LineCircleTransformer> {
    private CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    private int segments = 32;
    private float radius = 1.0f;
    private float lineWidth = 1.0f;

    public LineCircleBuilder axis(CircleLikeShape.CircleAxis circleAxis) { this.circleAxis = circleAxis; return this; }
    public LineCircleBuilder segments(int segments) { this.segments = segments; return this; }
    public LineCircleBuilder radius(float radius) { this.radius = radius; return this; }
    public LineCircleBuilder lineWidth(float lineWidth) { this.lineWidth = lineWidth; return this; }

    @Override
    public Shape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new LineCircleShape(type, t, circleAxis, center, segments, radius, lineWidth, color, seeThrough);
    }
}
