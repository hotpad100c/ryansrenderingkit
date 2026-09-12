package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.cylinder.ConeWireframeShape;

public class ConeWireframeBuilder extends CylinderWireframeBuilder {
    @Override
    public ConeWireframeShape build() {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        ConeWireframeShape shape = new ConeWireframeShape(t, circleAxis, center, segments, radius, height, width, color, seeThrough);
        applyCommon(shape);
        return shape;
    }

    @Deprecated
    public ConeWireframeShape build(Shape.RenderingType type) {
        return build();
    }
}