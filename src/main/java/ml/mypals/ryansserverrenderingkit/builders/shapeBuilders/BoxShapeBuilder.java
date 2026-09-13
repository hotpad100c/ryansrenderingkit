package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.basics.BoxLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.box.BoxShape;
import net.minecraft.world.phys.Vec3;

public class BoxShapeBuilder extends BaseBuilder<BoxShapeBuilder, BoxLikeShape.BoxTransformer>
        implements BoxBuilder<BoxShapeBuilder> {
    private Vec3 min = Vec3.ZERO;
    private Vec3 max = new Vec3(1, 1, 1);
    private Vec3 dimensions = new Vec3(1, 1, 1);
    private BoxShape.BoxConstructionType constructionType = BoxShape.BoxConstructionType.CORNERS;

    @Override
    public BoxShapeBuilder min(Vec3 min) {
        this.min = min;
        return this;
    }

    @Override
    public BoxShapeBuilder max(Vec3 max) {
        this.max = max;
        return this;
    }

    @Override
    public BoxShapeBuilder aabb(Vec3 min, Vec3 max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public BoxShapeBuilder size(Vec3 size) {
        this.dimensions = size;
        return this;
    }

    @Override
    public BoxShapeBuilder construction(BoxShape.BoxConstructionType type) {
        this.constructionType = type;
        return this;
    }

    @Override
    public BoxShape build() {
        BoxShape shape = constructionType == BoxShape.BoxConstructionType.CORNERS
                ? new BoxShape(getTransformer(), min, max, color, seeThrough, constructionType)
                : new BoxShape(getTransformer(), center, dimensions, color, seeThrough, constructionType);
        return applyCommon(shape);
    }
}
