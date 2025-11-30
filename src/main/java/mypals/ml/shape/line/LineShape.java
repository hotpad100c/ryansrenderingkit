package mypals.ml.shape.line;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.collision.RayModelIntersection;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.basics.core.TwoPointsLineShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static mypals.ml.utils.Helpers.createViewMatrix;

public class LineShape extends Shape implements TwoPointsLineShape {

    public LineShape(RenderingType type,
                     Consumer<TwoPointsLineTransformer> transform,
                     Vec3 start, Vec3 end,
                     Color color, float lineWidth,
                     boolean seeThrough) {

        super(type, color, seeThrough);

        this.transformer = new TwoPointsLineTransformer(this,start,end,lineWidth,Vec3.ZERO);
        this.transformFunction = (defaultTransformer) ->
                transform.accept((TwoPointsLineTransformer) this.transformer);

        syncLastToTarget();
        generateRawGeometry(false);
        this.transformer.setShapeWorldPivot(calculateShapeCenterPos());
    }

    public Vec3 calculateShapeCenterPos() {
        double centerX = (getStart(false).x + getEnd(false).x) / 2.0;
        double centerY = (getStart(false).y + getEnd(false).y) / 2.0;
        double centerZ = (getStart(false).z + getEnd(false).z) / 2.0;
        return new Vec3(centerX, centerY, centerZ);
    }
    public void forceSetStart(Vec3 start) {
        setStart(start);
        ((TwoPointsLineTransformer)this.transformer).lineModelInfo.startPointTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetEnd(Vec3 end) {
        setEnd(end);
        ((TwoPointsLineTransformer)this.transformer).lineModelInfo.endPointTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }
    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((TwoPointsLineTransformer)this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
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
    public boolean shouldDraw() {
        List<Vec3> vertices = this.getModel(true);
        if (vertices.isEmpty()) return false;

        Minecraft client = Minecraft.getInstance();
        Camera camera = client.gameRenderer.getMainCamera();
        GameRenderer gameRenderer = client.gameRenderer;

        Vec3 center = this.transformer.getWorldPivot().add(this.transformer.getLocalPivot());

        Matrix4f viewMatrix = createViewMatrix(camera);

        float fov = client.options.fov().get().floatValue();
        Matrix4f projectionMatrix = gameRenderer.getProjectionMatrix(fov);

        Matrix4f mvp = new Matrix4f(projectionMatrix);
        mvp.mul(viewMatrix);

        if (isVertexInFrustum(center, mvp)) return true;

        for (Vec3 v : vertices) {
            if (isVertexInFrustum(v, mvp)) return true;
        }

        for (int i = 0; i < vertices.size() - 1; i++) {
            Vec3 a = vertices.get(i);
            Vec3 b = vertices.get(i + 1);
            if (LineLikeShape.isSegmentInFrustum(a, b, mvp)) return true;
        }

        return false;
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

