package mypals.ml.shape.box;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableLine;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BoxWireframeShape extends BoxShape implements DrawableLine {

    public final float edgeWidth;

    public BoxWireframeShape(RenderingType type,
                             Consumer<BoxTransformer> transform,
                             Vec3 vec1,
                             Vec3 vec2,
                             Color edgeColor,
                             boolean seeThrough,
                             float edgeWidth,
                             BoxConstructionType constructionType) {

        super(type, transform, vec1, vec2,edgeColor, seeThrough, constructionType);
        this.edgeWidth = Math.max(0.1f, edgeWidth);
        generateRawGeometry(false);
    }

    public BoxWireframeShape(RenderingType type,
                             Consumer<BoxTransformer> transform,
                             Vec3 vec1,
                             Vec3 vec2,
                             BoxConstructionType constructionType) {
        this(type, transform, vec1, vec2, Color.WHITE, false, 1.0f, constructionType);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();

        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 c = bt.getLocalPivot();
        Vec3 d = bt.getDimension(lerp);

        double hx = d.x * 0.5;
        double hy = d.y * 0.5;
        double hz = d.z * 0.5;

        Vec3 v0 = new Vec3(c.x - hx, c.y - hy, c.z - hz);
        Vec3 v1 = new Vec3(c.x + hx, c.y - hy, c.z - hz);
        Vec3 v2 = new Vec3(c.x + hx, c.y - hy, c.z + hz);
        Vec3 v3 = new Vec3(c.x - hx, c.y - hy, c.z + hz);
        Vec3 v4 = new Vec3(c.x - hx, c.y + hy, c.z - hz);
        Vec3 v5 = new Vec3(c.x + hx, c.y + hy, c.z - hz);
        Vec3 v6 = new Vec3(c.x + hx, c.y + hy, c.z + hz);
        Vec3 v7 = new Vec3(c.x - hx, c.y + hy, c.z + hz);

        model_vertexes.add(v0); // 0
        model_vertexes.add(v1); // 1
        model_vertexes.add(v2); // 2
        model_vertexes.add(v3); // 3
        model_vertexes.add(v4); // 4
        model_vertexes.add(v5); // 5
        model_vertexes.add(v6); // 6
        model_vertexes.add(v7); // 7

        indexBuffer = new int[] {
                // Bottom
                0, 1,   1, 2,   2, 3,   3, 0,
                // Top
                4, 5,   5, 6,   6, 7,   7, 4,
                // Vert
                0, 4,   1, 5,   2, 6,   3, 7
        };
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        RenderSystem.lineWidth(edgeWidth);
        builder.putColor(baseColor);

        for (int i = 0; i < indexBuffer.length; i += 2) {
            Vec3 start = model_vertexes.get(indexBuffer[i]);
            Vec3 end   = model_vertexes.get(indexBuffer[i + 1]);
            addLineSegment(builder, start, end);
        }
    }
    private void addLineSegment(VertexBuilder builder, Vec3 start, Vec3 end) {
        Vec3 dir = end.subtract(start);
        dir.normalize();

        Vec3 normal = new Vec3(dir.x(), dir.y(), dir.z());

        builder.putVertex(start, normal);
        builder.putVertex(end, normal);
    }
}
