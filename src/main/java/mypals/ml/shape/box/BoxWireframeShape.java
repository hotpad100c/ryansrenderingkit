package mypals.ml.shape.box;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.drawTypes.DrawableLine;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.function.BiConsumer;

public class BoxWireframeShape extends BoxShape implements DrawableLine {
    public final Color edgeputColor;
    public float edgeWidth;
    public BoxWireframeShape(RenderingType type,
                             BiConsumer<BoxTransformer, Shape> transform, Vec3d min, Vec3d max,
                             Color edgeputColor, boolean seeThrough, float edgeWidth,BoxConstructionType constructionType) {
        super(type, transform,min,max,seeThrough,constructionType);
        this.edgeputColor = edgeputColor;
        this.edgeWidth = edgeWidth;
        this.seeThrough = seeThrough;
    }
    public BoxWireframeShape(RenderingType type, Vec3d min, Vec3d max,
                             Color edgeputColor, float edgeWidth, boolean seeThrough,BoxConstructionType boxConstructionType) {
        this(type, (transformer,shape)-> {},min, max, edgeputColor, seeThrough, edgeWidth,boxConstructionType);
    }
    @Override
    public void draw(ShapeBuilder builder) {
        RenderSystem.lineWidth(edgeWidth);
        renderEdges(builder);
    }

    private void renderEdges(ShapeBuilder shapeBuilder) {
        shapeBuilder.putColor(edgeputColor);
        Vec3d dimensions = this.getDimensions();
        Vec3d center = this.getCenter();

        Vec3d halfDimensions = dimensions.multiply(0.5);
        double halfWidth = halfDimensions.x;
        double halfHeight = halfDimensions.y;
        double halfLength = halfDimensions.z;

        Vec3d v1 = new Vec3d(center.x - halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3d v2 = new Vec3d(center.x + halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3d v3 = new Vec3d(center.x + halfWidth, center.y - halfHeight, center.z + halfLength);
        Vec3d v4 = new Vec3d(center.x - halfWidth, center.y - halfHeight, center.z + halfLength);

        Vec3d v5 = new Vec3d(center.x - halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3d v6 = new Vec3d(center.x + halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3d v7 = new Vec3d(center.x + halfWidth, center.y + halfHeight, center.z + halfLength);
        Vec3d v8 = new Vec3d(center.x - halfWidth, center.y + halfHeight, center.z + halfLength);

        addLineSegment(shapeBuilder, v1, v2);
        addLineSegment(shapeBuilder, v2, v3);
        addLineSegment(shapeBuilder, v3, v4);
        addLineSegment(shapeBuilder, v4, v1);

        addLineSegment(shapeBuilder, v5, v6);
        addLineSegment(shapeBuilder, v6, v7);
        addLineSegment(shapeBuilder, v7, v8);
        addLineSegment(shapeBuilder, v8, v5);

        addLineSegment(shapeBuilder, v1, v5);
        addLineSegment(shapeBuilder, v2, v6);
        addLineSegment(shapeBuilder, v3, v7);
        addLineSegment(shapeBuilder, v4, v8);
    }

    private void addLineSegment(ShapeBuilder shapeBuilder, Vec3d start, Vec3d end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double dz = end.getZ() - start.getZ();

        double distanceInv = 1.0 / Math.sqrt(dx * dx + dy * dy + dz * dz);
        Vec3d normal = new Vec3d(dx * distanceInv, dy * distanceInv, dz * distanceInv);

        shapeBuilder.putVertex(start, normal);
        shapeBuilder.putVertex(end, normal);
    }

}
