package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.basics.core.TwoPointsLineShape;
import ml.mypals.ryansserverrenderingkit.shape.line.LineShape;
import net.minecraft.world.phys.Vec3;

public class LineBuilder extends BaseBuilder<LineBuilder, TwoPointsLineShape.TwoPointsLineTransformer> {
    private Vec3 start = Vec3.ZERO;
    private Vec3 end = Vec3.ZERO;
    private float lineWidth = 0.05f;

    public LineBuilder start(Vec3 start) {
        this.start = start;
        return this;
    }

    public LineBuilder end(Vec3 end) {
        this.end = end;
        return this;
    }

    public LineBuilder lineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    @Override
    public LineShape build() {
        var t = getTransformer();
        LineShape line = new LineShape(t, start, end, color, lineWidth, seeThrough);
        return applyCommon(line);
    }
}