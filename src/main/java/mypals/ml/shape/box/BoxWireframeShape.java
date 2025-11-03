package mypals.ml.shape.box;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableLine;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.BiConsumer;

public class BoxWireframeShape extends BoxShape implements DrawableLine {
    public final Color edgeputColor;
    public float edgeWidth;
    public BoxWireframeShape(RenderingType type,
                             BiConsumer<BoxTransformer, Shape> transform, Vec3 min, Vec3 max,
                             Color edgeputColor, boolean seeThrough, float edgeWidth,BoxConstructionType constructionType) {
        super(type, transform,min,max,seeThrough,constructionType);
        this.edgeputColor = edgeputColor;
        this.edgeWidth = edgeWidth;
        this.seeThrough = seeThrough;
    }
    @Override
    public void draw(VertexBuilder builder) {
        RenderSystem.lineWidth(edgeWidth);
        renderEdges(builder);
    }

    private void renderEdges(VertexBuilder vertexBuilder) {
        vertexBuilder.putColor(edgeputColor);
        Vec3 dimensions = this.getDimensions();
        Vec3 center = this.getCenter();

        Vec3 halfDimensions = dimensions.scale(0.5);
        double halfWidth = halfDimensions.x;
        double halfHeight = halfDimensions.y;
        double halfLength = halfDimensions.z;

        Vec3 v1 = new Vec3(center.x - halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3 v2 = new Vec3(center.x + halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3 v3 = new Vec3(center.x + halfWidth, center.y - halfHeight, center.z + halfLength);
        Vec3 v4 = new Vec3(center.x - halfWidth, center.y - halfHeight, center.z + halfLength);

        Vec3 v5 = new Vec3(center.x - halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3 v6 = new Vec3(center.x + halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3 v7 = new Vec3(center.x + halfWidth, center.y + halfHeight, center.z + halfLength);
        Vec3 v8 = new Vec3(center.x - halfWidth, center.y + halfHeight, center.z + halfLength);

        addLineSegment(vertexBuilder, v1, v2);
        addLineSegment(vertexBuilder, v2, v3);
        addLineSegment(vertexBuilder, v3, v4);
        addLineSegment(vertexBuilder, v4, v1);

        addLineSegment(vertexBuilder, v5, v6);
        addLineSegment(vertexBuilder, v6, v7);
        addLineSegment(vertexBuilder, v7, v8);
        addLineSegment(vertexBuilder, v8, v5);

        addLineSegment(vertexBuilder, v1, v5);
        addLineSegment(vertexBuilder, v2, v6);
        addLineSegment(vertexBuilder, v3, v7);
        addLineSegment(vertexBuilder, v4, v8);
    }

    private void addLineSegment(VertexBuilder vertexBuilder, Vec3 start, Vec3 end) {
        double dx = end.x() - start.x();
        double dy = end.y() - start.y();
        double dz = end.z() - start.z();

        double distanceInv = 1.0 / Math.sqrt(dx * dx + dy * dy + dz * dz);
        Vec3 normal = new Vec3(dx * distanceInv, dy * distanceInv, dz * distanceInv);

        vertexBuilder.putVertex(start, normal);
        vertexBuilder.putVertex(end, normal);
    }

}
