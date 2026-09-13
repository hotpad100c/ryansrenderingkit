package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.model.ObjModelShape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import net.minecraft.resources.Identifier;

public class ObjModelBuilder extends BaseBuilder<ObjModelBuilder, DefaultTransformer> {
    private Identifier resourceLocation;

    public ObjModelBuilder model(Identifier resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    @Override
    public ObjModelShape build() {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        ObjModelShape shape = new ObjModelShape(t, resourceLocation, center, color, seeThrough);
        applyCommon(shape);
        return shape;
    }
}
