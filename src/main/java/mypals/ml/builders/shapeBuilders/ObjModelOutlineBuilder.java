package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.model.ObjModelShapeOutline;
import net.minecraft.resources.ResourceLocation;

public class ObjModelOutlineBuilder extends BaseBuilder<ObjModelOutlineBuilder, LineLikeShape.SimpleLineTransformer> {
    private ResourceLocation resourceLocation;
    private float lineWidth = 1.0f;

    public ObjModelOutlineBuilder model(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }
    public ObjModelOutlineBuilder lineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    @Override
    public Shape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new ObjModelShapeOutline(type, t, resourceLocation, center, lineWidth, color, seeThrough);
    }
}
