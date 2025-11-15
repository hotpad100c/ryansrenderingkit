package mypals.ml.shape.line;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.collision.RayModelIntersection;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.TwoPointsLineShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LineShape extends Shape implements TwoPointsLineShape {

    public LineShape(RenderingType type,
                     Consumer<TwoPointsLineTransformer> transform,
                     Vec3 start, Vec3 end,
                     Color color, float lineWidth,
                     boolean seeThrough) {

        super(type, color, seeThrough);

        this.transformer = new TwoPointsLineTransformer(this,start,end,lineWidth,Vec3.ZERO);
        this.transformer.setShapeWorldPivot(calculateShapeCenterPos());
        this.transformFunction = (defaultTransformer) ->
                transform.accept((TwoPointsLineTransformer) this.transformer);

        syncLastToTarget();
        generateRawGeometry(false);
    }

    public Vec3 calculateShapeCenterPos() {
        double centerX = (getStart(false).x + getEnd(false).x) / 2.0;
        double centerY = (getStart(false).y + getEnd(false).y) / 2.0;
        double centerZ = (getStart(false).z + getEnd(false).z) / 2.0;
        return new Vec3(centerX, centerY, centerZ);
    }

    @Override
    public void setStart(Vec3 start) {
        ((TwoPointsLineTransformer)this.transformer).setStart(start);
    }

    @Override
    public void setEnd(Vec3 end) {
        ((TwoPointsLineTransformer)this.transformer).setEnd(end);
    }

    @Override
    public void setLineWidth(float width) {
        ((TwoPointsLineTransformer)this.transformer).setWidth(width);
    }

    @Override
    public float getLineWidth(boolean lerp) {
        return ((TwoPointsLineTransformer)this.transformer).getWidth(lerp);
    }

    @Override
    public Vec3 getStart(boolean lerp) { return ((TwoPointsLineTransformer)this.transformer).getStart(lerp);}

    @Override
    public Vec3 getEnd(boolean lerp) { return ((TwoPointsLineTransformer)this.transformer).getEnd(lerp);}

    public float getWidth(boolean lerp) { return ((TwoPointsLineTransformer)this.transformer).getWidth(lerp);}

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();

        Vec3 start = getStart(lerp);
        Vec3 end = getEnd(lerp);
        Vec3 center = calculateShapeCenterPos();


        this.transformer.setShapeWorldPivot(center);
        this.transformer.world.position.syncLastToTarget();

        Vec3 localA = start.subtract(center);
        Vec3 localB = end.subtract(center);

        model_vertexes.add(localA);
        model_vertexes.add(localB);

        this.indexBuffer = new int[] { 0, 1 };
    }
    @Override
    public RayModelIntersection.HitResult isPlayerLookingAt(){
        return new RayModelIntersection.HitResult(false,null,-1);
    }
    protected void drawInternal(VertexBuilder builder) {
        RenderSystem.lineWidth(getWidth(true));
        builder.putColor(baseColor);
        addLineSegment(builder,model_vertexes.getFirst(),model_vertexes.getLast());
    }
}

