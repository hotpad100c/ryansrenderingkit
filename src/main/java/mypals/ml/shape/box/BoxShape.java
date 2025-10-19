package mypals.ml.shape.box;

import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shape.basics.core.QuadLikeShape;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;


import java.awt.*;
import java.util.function.BiConsumer;


public class BoxShape extends mypals.ml.shape.Shape implements QuadLikeShape, BoxLikeShape {

    public final Vec3d min;
    public final Vec3d max;
    public final Color faceputColor;
    public BoxShape(RenderingType type,
                    BiConsumer<MatrixStack, Shape> transform,
                    Vec3d min,
                    Vec3d max,
                    Color faceputColor,
                    boolean seeThrough)
    {
        super(type, transform, seeThrough);
        this.min = new Vec3d(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z));
        this.max = new Vec3d(Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
        this.faceputColor = faceputColor;
    }
    public BoxShape(RenderingType type,
                    Vec3d min,
                    Vec3d max,
                    Color faceputColor,
                    boolean seeThrough)
    {
        this(type, (ms, shape) -> {}, min, max, faceputColor, seeThrough);
    }
    @Override
    public void draw(ShapeBuilder builder) {
        this.renderFaces(builder);
    }

    private void renderFaces(ShapeBuilder shapeBuilder) {

        shapeBuilder.putColor(faceputColor);

        shapeBuilder.putVertex((float) min.x, (float) min.y, (float) min.z);
        shapeBuilder.putVertex((float) min.x, (float) max.y, (float) min.z);
        shapeBuilder.putVertex((float) max.x, (float) max.y, (float) min.z);
        shapeBuilder.putVertex((float) max.x, (float) min.y, (float) min.z);

        shapeBuilder.putVertex((float) max.x, (float) min.y, (float) max.z);
        shapeBuilder.putVertex((float) max.x, (float) max.y, (float) max.z);
        shapeBuilder.putVertex((float) min.x, (float) max.y, (float) max.z);
        shapeBuilder.putVertex((float) min.x, (float) min.y, (float) max.z);

        shapeBuilder.putVertex((float) min.x, (float) min.y, (float) max.z);
        shapeBuilder.putVertex((float) min.x, (float) max.y, (float) max.z);
        shapeBuilder.putVertex((float) min.x, (float) max.y, (float) min.z);
        shapeBuilder.putVertex((float) min.x, (float) min.y, (float) min.z);

        shapeBuilder.putVertex((float) max.x, (float) min.y, (float) min.z);
        shapeBuilder.putVertex((float) max.x, (float) max.y, (float) min.z);
        shapeBuilder.putVertex((float) max.x, (float) max.y, (float) max.z);
        shapeBuilder.putVertex((float) max.x, (float) min.y, (float) max.z);

        shapeBuilder.putVertex((float) min.x, (float) min.y, (float) max.z);
        shapeBuilder.putVertex((float) min.x, (float) min.y, (float) min.z);
        shapeBuilder.putVertex((float) max.x, (float) min.y, (float) min.z);
        shapeBuilder.putVertex((float) max.x, (float) min.y, (float) max.z);

        shapeBuilder.putVertex((float) max.x, (float) max.y, (float) max.z);
        shapeBuilder.putVertex((float) max.x, (float) max.y, (float) min.z);
        shapeBuilder.putVertex((float) min.x, (float) max.y, (float) min.z);
        shapeBuilder.putVertex((float) min.x, (float) max.y, (float) max.z);
    }


    @Override
    public Vec3d getMin() {
        return min;
    }

    @Override
    public Vec3d getMax() {
        return max;
    }

    @Override
    public Vec3d getCenter() {
        return null;
    }
}
