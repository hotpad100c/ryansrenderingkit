package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.cylinder.ConeShape;
import mypals.ml.shape.cylinder.CylinderShape;

public class ConeBuilder extends BaseBuilder<ConeBuilder, CylinderShape.CylinderTransformer> {
    private CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    private int segments = 32;
    private float radius = 1.0f;
    private float height = 1.0f;

    public ConeBuilder axis(CircleLikeShape.CircleAxis circleAxis) {
        this.circleAxis = circleAxis;
        return this;
    }

    public ConeBuilder segments(int segments) {
        this.segments = segments;
        return this;
    }

    public ConeBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    public ConeBuilder height(float height) {
        this.height = height;
        return this;
    }

    @Override
    public ConeShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new ConeShape(type, t, circleAxis, center, segments, radius, height, color, seeThrough);
    }
}