package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.box.BoxShape;
import mypals.ml.shape.box.BoxWireframeShape;
import mypals.ml.shape.box.WireframedBoxShape;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.BiConsumer;

public class WireframedBoxBuilder extends BaseBuilder<WireframedBoxBuilder,BoxLikeShape.BoxTransformer>
        implements BoxBuilder<WireframedBoxBuilder> {

    private Vec3 min = Vec3.ZERO;
    private Vec3 max = new Vec3(1, 1, 1);
    private Color edgeColor = Color.WHITE;
    private float edgeWidth = 1.0f;
    private boolean lineSeeThrough = false;
    private BoxShape.BoxConstructionType constructionType = BoxShape.BoxConstructionType.CORNERS;

    @Override public WireframedBoxBuilder min(Vec3 min) { this.min = min; return this; }
    @Override public WireframedBoxBuilder max(Vec3 max) { this.max = max; return this; }
    @Override public WireframedBoxBuilder aabb(Vec3 min, Vec3 max) { this.min = min; this.max = max; return this; }
    @Override public WireframedBoxBuilder size(Vec3 size) { this.max = this.min.add(size); return this; }
    @Override public WireframedBoxBuilder construction(BoxShape.BoxConstructionType type) { this.constructionType = type; return this; }

    public WireframedBoxBuilder edgeColor(Color edgeColor) { this.edgeColor = edgeColor; return this; }
    public WireframedBoxBuilder edgeWidth(float edgeWidth) { this.edgeWidth = edgeWidth; return this; }
    public WireframedBoxBuilder lineSeeThrough(boolean lineSeeThrough) { this.lineSeeThrough = lineSeeThrough; return this; }

    @Override
    public WireframedBoxShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new WireframedBoxShape(
                type, t, min, max,
                color,
                edgeColor,
                edgeWidth,
                seeThrough,
                lineSeeThrough,
                constructionType
        );
    }
}