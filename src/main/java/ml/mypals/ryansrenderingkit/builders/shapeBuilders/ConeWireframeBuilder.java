package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.cylinder.ConeWireframeShape;

public class ConeWireframeBuilder extends CylinderWireframeBuilder {
    @Override
    public ConeWireframeShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new ConeWireframeShape(type, t, circleAxis, center, segments, radius, height, width, color, seeThrough);
    }
}