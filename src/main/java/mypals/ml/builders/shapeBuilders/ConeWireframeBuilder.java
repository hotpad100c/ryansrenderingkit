package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.cylinder.ConeWireframeShape;
import mypals.ml.shape.cylinder.CylinderWireframeShape;

public class ConeWireframeBuilder extends CylinderWireframeBuilder {
    @Override
    public Shape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new ConeWireframeShape(type, t, circleAxis, center, segments, radius, height,width, color, seeThrough);
    }
}