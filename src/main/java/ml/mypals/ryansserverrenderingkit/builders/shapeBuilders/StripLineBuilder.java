package ml.mypals.ryansserverrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansserverrenderingkit.shape.basics.core.LineLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.line.StripLineShape;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StripLineBuilder extends BaseBuilder<StripLineBuilder, LineLikeShape.SimpleLineTransformer> {
    private final List<Vec3> vertexes = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private float lineWidth = 0.05f;

    public StripLineBuilder vertexColors(List<Color> colors) {
        this.colors.clear();
        this.colors.addAll(colors);
        return this;
    }

    public StripLineBuilder addVertex(Vec3 v) {
        this.vertexes.add(v);
        return this;
    }

    public StripLineBuilder vertexes(List<Vec3> vertexes) {
        this.vertexes.clear();
        this.vertexes.addAll(vertexes);
        return this;
    }

    public StripLineBuilder lineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    @Override
    public StripLineShape build() {
        if (vertexes.size() < 2) throw new IllegalStateException("StripLine needs at least 2 vertices");
        var t = getTransformer();
        StripLineShape stripLineShape = new StripLineShape(t, List.copyOf(vertexes), lineWidth, color, seeThrough);
        stripLineShape.setVertexColors(colors);
        return applyCommon(stripLineShape);
    }
}