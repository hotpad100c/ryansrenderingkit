package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.core.LineLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.model.ObjModelShapeOutline;
import net.minecraft.resources.Identifier;

public class ObjModelOutlineBuilder extends BaseBuilder<ObjModelOutlineBuilder, LineLikeShape.SimpleLineTransformer> {
    private Identifier resourceLocation;
    private float lineWidth = 1.0f;

    public ObjModelOutlineBuilder model(Identifier resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    public ObjModelOutlineBuilder lineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    @Override
    public ObjModelShapeOutline build() {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        ObjModelShapeOutline shape = new ObjModelShapeOutline(t, resourceLocation, center, lineWidth, color, seeThrough);
        applyCommon(shape);
        return shape;
    }

    @Deprecated
    public ObjModelShapeOutline build(Shape.RenderingType type) {
        return build();
    }
}
