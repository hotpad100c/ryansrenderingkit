package mypals.ml.shape.box;

import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.function.BiConsumer;

public class WireframedBoxShape extends Shape implements BoxLikeShape {
    public final Vec3d min;
    public final Vec3d max;
    public final Color faceputColor;
    public final Color edgeputColor;
    public final float edgeWidth;
    public boolean lineSeeThrough = false;

    public WireframedBoxShape(RenderingType type,
                              BiConsumer<MatrixStack, Shape> transform,
                              Vec3d min,
                              Vec3d max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough)
    {
        super(type, transform, seeThrough);
        this.min = new Vec3d(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z));
        this.max = new Vec3d(Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
        this.faceputColor = faceputColor;
        this.edgeputColor = edgeputColor;
        this.edgeWidth = edgeWidth;
        this.lineSeeThrough = lineSeeThrough;
        this.isGroupedShape = true;
        this.centerPoint = getCenter();
    }
    public WireframedBoxShape(RenderingType type,
                              Vec3d min,
                              Vec3d max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough)
    {
        this(type, (ms, shape) -> {},min,max,faceputColor,edgeputColor,edgeWidth, seeThrough,lineSeeThrough);
    }
    @Override
    public void addGroup(Identifier identifier) {
        ShapeManagers.LINES_SHAPE_MANAGER.addShape(
                identifier,
                new BoxWireframeShape(
                        this.type,
                        this.transform,
                        this.min,
                        this.max,
                        this.edgeputColor,
                        this.lineSeeThrough,
                        this.edgeWidth
                )
        );
        ShapeManagers.QUADS_SHAPE_MANAGER.addShape(
                identifier,
                new BoxShape(
                    this.type,
                    this.transform,
                    this.min,
                    this.max,
                    this.faceputColor,
                    this.seeThrough
                )
        );
    }

    @Override
    public Vec3d getMin() {
        return this.min;
    }

    @Override
    public Vec3d getMax() {
        return this.max;
    }
}
