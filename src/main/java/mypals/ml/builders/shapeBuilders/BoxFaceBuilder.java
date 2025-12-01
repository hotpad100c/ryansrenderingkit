package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.box.BoxFaceShape;
import mypals.ml.shape.box.BoxShape;
import net.minecraft.world.phys.Vec3;

public class BoxFaceBuilder extends BaseBuilder<BoxFaceBuilder, BoxLikeShape.BoxTransformer> implements BoxBuilder<BoxFaceBuilder> {
    private Vec3 min = Vec3.ZERO;
    private Vec3 max = new Vec3(1, 1, 1);
    private Vec3 dim = new Vec3(1, 1, 1);
    private BoxShape.BoxConstructionType constructionType = BoxShape.BoxConstructionType.CORNERS;

    @Override
    public BoxFaceBuilder min(Vec3 min) {
        this.min = min;
        return this;
    }

    @Override
    public BoxFaceBuilder max(Vec3 max) {
        this.max = max;
        return this;
    }

    @Override
    public BoxFaceBuilder aabb(Vec3 min, Vec3 max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public BoxFaceBuilder size(Vec3 size) {
        this.dim = size;
        return this;
    }

    @Override
    public BoxFaceBuilder construction(BoxShape.BoxConstructionType type) {
        this.constructionType = type;
        return this;
    }

    @Override
    public BoxFaceShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        if (constructionType == BoxShape.BoxConstructionType.CORNERS) {
            return new BoxFaceShape(type, t, min, max, color, seeThrough, constructionType);
        } else {
            return new BoxFaceShape(type, t, center, dim, color, seeThrough, constructionType);
        }

    }
}