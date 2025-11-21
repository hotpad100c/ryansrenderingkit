package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.box.BoxShape;
import mypals.ml.shape.box.BoxWireframeShape;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class BoxWireframeBuilder extends BaseBuilder<BoxWireframeBuilder, BoxLikeShape.BoxTransformer>
        implements BoxBuilder<BoxWireframeBuilder> {

    private Vec3 min = Vec3.ZERO;
    private Vec3 max = new Vec3(1, 1, 1);
    private float edgeWidth = 1.0f;
    private BoxShape.BoxConstructionType constructionType = BoxShape.BoxConstructionType.CORNERS;

    @Override public BoxWireframeBuilder min(Vec3 min) { this.min = min; return this; }
    @Override public BoxWireframeBuilder max(Vec3 max) { this.max = max; return this; }
    @Override public BoxWireframeBuilder aabb(Vec3 min, Vec3 max) { this.min = min; this.max = max; return this; }
    @Override public BoxWireframeBuilder size(Vec3 size) { this.max = this.min.add(size); return this; }
    @Override public BoxWireframeBuilder construction(BoxShape.BoxConstructionType type) { this.constructionType = type; return this; }

    public BoxWireframeBuilder edgeWidth(float edgeWidth) { this.edgeWidth = edgeWidth; return this; }

    @Override
    public BoxWireframeShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new BoxWireframeShape(
                type, t, min, max,
                color, seeThrough, edgeWidth, constructionType
        );
    }
}