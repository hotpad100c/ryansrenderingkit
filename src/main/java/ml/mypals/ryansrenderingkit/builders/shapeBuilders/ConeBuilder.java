package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansrenderingkit.shape.cylinder.ConeShape;
import ml.mypals.ryansrenderingkit.shape.cylinder.CylinderShape;

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