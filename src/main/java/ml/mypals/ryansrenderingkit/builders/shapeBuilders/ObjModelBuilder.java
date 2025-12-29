package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.model.ObjModelShape;
import ml.mypals.ryansrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.resources.ResourceLocation;

public class ObjModelBuilder extends BaseBuilder<ObjModelBuilder, DefaultTransformer> {
    private ResourceLocation resourceLocation;

    public ObjModelBuilder model(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    @Override
    public ObjModelShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new ObjModelShape(type, t, resourceLocation, center, color, seeThrough);
    }
}