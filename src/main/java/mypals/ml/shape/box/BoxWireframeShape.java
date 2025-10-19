package mypals.ml.shape.box;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.basics.core.LineLikeShape;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.function.BiConsumer;

public class BoxWireframeShape extends mypals.ml.shape.Shape implements BoxLikeShape, LineLikeShape {
    public final Color edgeputColor;
    public final Vec3d min;
    public final Vec3d max;
    public final float edgeWidth;
    public BoxWireframeShape(RenderingType type,
                             BiConsumer<MatrixStack, Shape> transform, Vec3d min, Vec3d max,
                             Color edgeputColor, boolean seeThrough, float edgeWidth) {
        super(type, transform,seeThrough);
        this.edgeputColor = edgeputColor;
        this.min = min;
        this.max = max;
        this.edgeWidth = edgeWidth;
        this.seeThrough = seeThrough;
    }
    public BoxWireframeShape(RenderingType type, Vec3d min, Vec3d max,
                             Color edgeputColor, float edgeWidth, boolean seeThrough) {
        this(type, (matrixStack,shape)-> {},min, max, edgeputColor, seeThrough, edgeWidth);
    }
    @Override
    public void draw(ShapeBuilder builder) {
        RenderSystem.lineWidth(edgeWidth);
        renderEdges(builder);
    }

    private void renderEdges(ShapeBuilder shapeBuilder) {
        shapeBuilder.putColor(edgeputColor);

        Vec3d v1 = new Vec3d(min.getX(), min.getY(), min.getZ());
        Vec3d v2 = new Vec3d(max.getX(), min.getY(), min.getZ());
        Vec3d v3 = new Vec3d(max.getX(), min.getY(), max.getZ());
        Vec3d v4 = new Vec3d(min.getX(), min.getY(), max.getZ());

        Vec3d v5 = new Vec3d(min.getX(), max.getY(), min.getZ());
        Vec3d v6 = new Vec3d(max.getX(), max.getY(), min.getZ());
        Vec3d v7 = new Vec3d(max.getX(), max.getY(), max.getZ());
        Vec3d v8 = new Vec3d(min.getX(), max.getY(), max.getZ());

        // Helper function to add vertices with normals for a line segment
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
    @Override
    public Vec3d getMin() {
        return min;
    }

    @Override
    public Vec3d getMax() {
        return max;
    }
}
