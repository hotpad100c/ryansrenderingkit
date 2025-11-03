package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.cylinder.CylinderShape;
import mypals.ml.shape.cylinder.CylinderWireframeShape;

public class CylinderWireframeBuilder extends BaseBuilder<CylinderWireframeBuilder, CylinderWireframeShape.CylinderWireframeTransformer> {
    protected CircleLikeShape.CircleAxis circleAxis = CircleLikeShape.CircleAxis.Y;
    protected int segments = 32;
    protected float radius = 1.0f;
    protected float height = 1.0f;
    protected float width = 1.0f;

    public CylinderWireframeBuilder axis(CircleLikeShape.CircleAxis circleAxis) { this.circleAxis = circleAxis; return this; }
    public CylinderWireframeBuilder segments(int segments) { this.segments = segments; return this; }
    public CylinderWireframeBuilder radius(float radius) { this.radius = radius; return this; }
    public CylinderWireframeBuilder height(float height) { this.height = height; return this; }
    public CylinderWireframeBuilder width(float width) { this.width = width; return this; }

    @Override
    public Shape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new CylinderWireframeShape(type, t, circleAxis, center, segments, radius, height,width, color, seeThrough);
    }
}