package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.basics.BoxLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.box.BoxShape;
import ml.mypals.ryansserverrenderingkit.shape.box.BoxWireframeShape;
import net.minecraft.world.phys.Vec3;

public class BoxWireframeBuilder extends BaseBuilder<BoxWireframeBuilder, BoxLikeShape.BoxTransformer>
        implements BoxBuilder<BoxWireframeBuilder> {

    private Vec3 min = Vec3.ZERO;
    private Vec3 max = new Vec3(1, 1, 1);
    private float edgeWidth = 0.05f;
    private BoxShape.BoxConstructionType constructionType = BoxShape.BoxConstructionType.CORNERS;

    @Override
    public BoxWireframeBuilder min(Vec3 min) {
        this.min = min;
        return this;
    }

    @Override
    public BoxWireframeBuilder max(Vec3 max) {
        this.max = max;
        return this;
    }

    @Override
    public BoxWireframeBuilder aabb(Vec3 min, Vec3 max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public BoxWireframeBuilder size(Vec3 size) {
        this.max = this.min.add(size);
        return this;
    }

    @Override
    public BoxWireframeBuilder construction(BoxShape.BoxConstructionType type) {
        this.constructionType = type;
        return this;
    }

    public BoxWireframeBuilder edgeWidth(float edgeWidth) {
        this.edgeWidth = edgeWidth;
        return this;
    }

    @Override
    public BoxWireframeShape build() {
        var t = getTransformer();
        BoxWireframeShape shape = new BoxWireframeShape(
                t, min, max,
                color, seeThrough, edgeWidth, constructionType
        );
        return applyCommon(shape);
    }
}