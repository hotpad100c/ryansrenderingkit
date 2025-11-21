package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.model.ObjModelShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

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