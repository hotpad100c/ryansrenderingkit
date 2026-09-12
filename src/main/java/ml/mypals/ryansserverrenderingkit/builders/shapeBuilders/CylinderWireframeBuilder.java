package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.cylinder.CylinderWireframeShape;

public class CylinderWireframeBuilder extends BaseBuilder<CylinderWireframeBuilder, CylinderWireframeShape.CylinderWireframeTransformer> {
    protected CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    protected int segments = 12;
    protected float radius = 1.0f;
    protected float height = 1.0f;
    protected float width = 0.05f;

    public CylinderWireframeBuilder axis(CircleLikeShape.CircleAxis circleAxis) {
        this.circleAxis = circleAxis;
        return this;
    }

    public CylinderWireframeBuilder segments(int segments) {
        this.segments = segments;
        return this;
    }

    public CylinderWireframeBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    public CylinderWireframeBuilder height(float height) {
        this.height = height;
        return this;
    }

    public CylinderWireframeBuilder width(float width) {
        this.width = width;
        return this;
    }

    @Override
    public CylinderWireframeShape build() {
        var t = getTransformer();
        CylinderWireframeShape shape = new CylinderWireframeShape(t, circleAxis, center, segments, radius, height, width, color, seeThrough);
        return applyCommon(shape);
    }
}