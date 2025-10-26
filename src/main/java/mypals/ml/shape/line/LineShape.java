package mypals.ml.shape.line;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.TwoPointsLineShape;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.function.BiConsumer;

public class LineShape extends Shape implements TwoPointsLineShape {
    public Vec3d start;
    public Vec3d end;
    public Color color;
    public float lineWidth;
    public LineShape(RenderingType type,
                     BiConsumer<TwoPointsLineTransformer, Shape> transform,
                     Vec3d start,
                     Vec3d end,
                     Color color,
                     float lineWidth,
                     boolean seeThrough)
    {
        super(type, seeThrough);
        this.start = start;
        this.end = end;
        this.color = color;
        setShapeCenterPos(calculateShapeCenterPos());
        this.lineWidth = lineWidth;
        this.transformer = new TwoPointsLineTransformer(this);
        this.transformer.setShapeCenterPos(this.getShapeCenterPos());
        this.transformFunction = (defaultTransformer,shape)->transform.accept((TwoPointsLineTransformer) this.transformer, shape);
        ((TwoPointsLineTransformer)this.transformer).setStart(this.getStart());
        ((TwoPointsLineTransformer)this.transformer).setEnd(this.getEnd());
        ((TwoPointsLineTransformer)this.transformer).setWidth(this.lineWidth);
        syncLastToTarget();
    }
    public LineShape(RenderingType type,
                     Vec3d start,
                     Vec3d end,
                     Color color,
                     float lineWidth,
                     boolean seeThrough)
    {
        this(type, (transformer, shape) -> {}, start, end, color,lineWidth, seeThrough);
    }
    @Override
    public Vec3d calculateShapeCenterPos() {
        double centerX = (getStart().x + getEnd().x) / 2.0;
        double centerY = (getStart().y + getEnd().y) / 2.0;
        double centerZ = (getStart().z + getEnd().z) / 2.0;
        return new Vec3d(centerX, centerY, centerZ);
    }

    @Override
    public void setShapeCenterPos(Vec3d newCenter) {
        super.setShapeCenterPos(newCenter);
        Vec3d currentCenter = calculateShapeCenterPos();
        Vec3d offset = newCenter.subtract(currentCenter);

        setStart(getStart().add(offset));
        setEnd(getEnd().add(offset));
    }


    @Override
    public void setStart(Vec3d start) {
        this.start = start;
    }

    @Override
    public void setEnd(Vec3d end) {
        this.end = end;
    }

    @Override
    public void setLineWidth(float width) {
        this.lineWidth = width;
    }

    @Override
    public Vec3d getStart() {
        return start;
    }
    @Override
    public Vec3d getEnd() {
        return end;
    }

    @Override
    public void draw(ShapeBuilder builder) {
        RenderSystem.lineWidth(lineWidth);
        builder.putColor(color);
        addLineSegment(builder,getStart(),getEnd());
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
