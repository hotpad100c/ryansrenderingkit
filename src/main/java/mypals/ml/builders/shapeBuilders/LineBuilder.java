package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.TwoPointsLineShape;
import mypals.ml.shape.line.LineShape;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class LineBuilder extends BaseBuilder<LineBuilder,TwoPointsLineShape.TwoPointsLineTransformer> {
    private Vec3 start = Vec3.ZERO;
    private Vec3 end = Vec3.ZERO;
    private float lineWidth = 1.0f;

    public LineBuilder start(Vec3 start) { this.start = start; return this; }
    public LineBuilder end(Vec3 end) { this.end = end; return this; }
    public LineBuilder lineWidth(float lineWidth) { this.lineWidth = lineWidth; return this; }

    @Override
    public Shape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new LineShape(type, t, start, end, color, lineWidth, seeThrough);
    }
}