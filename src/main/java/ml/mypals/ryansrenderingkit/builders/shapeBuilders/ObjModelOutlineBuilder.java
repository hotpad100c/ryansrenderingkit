package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.core.LineLikeShape;
import ml.mypals.ryansrenderingkit.shape.model.ObjModelShapeOutline;
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
    public ObjModelShapeOutline build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new ObjModelShapeOutline(type, t, resourceLocation, center, lineWidth, color, seeThrough);
    }
}
