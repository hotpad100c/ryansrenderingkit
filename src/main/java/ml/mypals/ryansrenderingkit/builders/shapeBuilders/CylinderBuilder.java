package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansrenderingkit.shape.cylinder.CylinderShape;

public class CylinderBuilder extends BaseBuilder<CylinderBuilder, CylinderShape.CylinderTransformer> {
    private CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    private int segments = 32;
    private float radius = 1.0f;
    private float height = 1.0f;

    public CylinderBuilder axis(CircleLikeShape.CircleAxis circleAxis) {
        this.circleAxis = circleAxis;
        return this;
    }

    public CylinderBuilder segments(int segments) {
        this.segments = segments;
        return this;
    }

    public CylinderBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    public CylinderBuilder height(float height) {
        this.height = height;
        return this;
    }

    @Override
    public CylinderShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new CylinderShape(type, t, circleAxis, center, segments, radius, height, color, seeThrough);
    }
}