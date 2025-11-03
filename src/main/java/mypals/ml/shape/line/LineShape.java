package mypals.ml.shape.line;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.TwoPointsLineShape;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.BiConsumer;

public class LineShape extends Shape implements TwoPointsLineShape {
    public Vec3 start;
    public Vec3 end;
    public Color color;
    public float lineWidth;
    public LineShape(RenderingType type,
                     BiConsumer<TwoPointsLineTransformer, Shape> transform,
                     Vec3 start,
                     Vec3 end,
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
    @Override
    public Vec3 calculateShapeCenterPos() {
        double centerX = (getStart().x + getEnd().x) / 2.0;
        double centerY = (getStart().y + getEnd().y) / 2.0;
        double centerZ = (getStart().z + getEnd().z) / 2.0;
        return new Vec3(centerX, centerY, centerZ);
    }

    @Override
    public void setShapeCenterPos(Vec3 newCenter) {
        super.setShapeCenterPos(newCenter);
        Vec3 currentCenter = calculateShapeCenterPos();
        Vec3 offset = newCenter.subtract(currentCenter);

        setStart(getStart().add(offset));
        setEnd(getEnd().add(offset));
    }


    @Override
    public void setStart(Vec3 start) {
        this.start = start;
    }

    @Override
    public void setEnd(Vec3 end) {
        this.end = end;
    }

    @Override
    public void setLineWidth(float width) {
        this.lineWidth = width;
    }

    @Override
    public Vec3 getStart() {
        return start;
    }
    @Override
    public Vec3 getEnd() {
        return end;
    }

    @Override
    public void draw(VertexBuilder builder) {
        RenderSystem.lineWidth(lineWidth);
        builder.putColor(color);
        addLineSegment(builder,getStart(),getEnd());
    }

}
