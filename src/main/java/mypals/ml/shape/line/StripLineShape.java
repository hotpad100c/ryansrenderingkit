package mypals.ml.shape.line;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.collision.RayModelIntersection;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.basics.core.StripLineLikeShape;
import mypals.ml.shape.basics.core.TwoPointsLineShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static mypals.ml.Helpers.createViewMatrix;

public class StripLineShape extends Shape implements StripLineLikeShape {

    private List<Vec3> vertexes = new ArrayList<>();
    private List<Color> vertexColors = new ArrayList<>();

    public StripLineShape(RenderingType type,
                          Consumer<SimpleLineTransformer> transform,
                          List<Vec3> vertexes,
                          float lineWidth,
                          Color color,
                          boolean seeThrough) {
        super(type, color, seeThrough);
        this.vertexes = new ArrayList<>(vertexes);
        this.transformer = new SimpleLineTransformer(this,lineWidth,this.calculateShapeCenterPos());
        this.transformFunction = (defaultTransformer) ->
                transform.accept((SimpleLineTransformer) this.transformer);

        syncLastToTarget();
        generateRawGeometry(false);
    }


    public Vec3 calculateShapeCenterPos() {
        if (vertexes.isEmpty()) return Vec3.ZERO;

        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3 v : vertexes) {
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
        }

        double n = vertexes.size();
        return new Vec3(sumX / n, sumY / n, sumZ / n);
    }
    @Override
    public RayModelIntersection.HitResult isPlayerLookingAt(){
        return new RayModelIntersection.HitResult(false,null,-1);
    }
    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();
        if (vertexes.size() < 2) return;
        Vec3 localCenter = calculateShapeCenterPos();
        transformer.setShapeWorldPivot(localCenter);
        for (Vec3 v : vertexes) {
            model_vertexes.add(v.subtract(localCenter));
        }

        int n = model_vertexes.size();
        indexBuffer = new int[n];
        for (int i = 0; i < n; i++) indexBuffer[i] = i;
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
    protected void drawInternal(VertexBuilder builder) {
        RenderSystem.lineWidth(getLineWidth(true));

        int n = model_vertexes.size();
        if (n < 2) return;;

        Vec3 first = model_vertexes.getFirst();
        builder.putColor(new Color(0, 0, 0, 0));
        builder.putVertex(first, Vec3.ZERO);
        for (int i = 0; i < n; i++) {
            Color vColor = baseColor;
            if (i < vertexColors.size()) vColor = vertexColors.get(i);
            builder.putColor(vColor);
            Vec3 normal;
            if (i == 0) {
                Vec3 dir = model_vertexes.get(1).subtract(model_vertexes.get(0));
                normal = dir.normalize();
            } else if (i == n - 1) {
                Vec3 dir = model_vertexes.get(n - 1).subtract(model_vertexes.get(n - 2));
                normal = dir.normalize();
            } else {
                Vec3 prevDir = model_vertexes.get(i).subtract(model_vertexes.get(i - 1));
                Vec3 nextDir = model_vertexes.get(i + 1).subtract(model_vertexes.get(i));
                normal = prevDir.add(nextDir).normalize();
                if (Double.isNaN(normal.x) || Double.isNaN(normal.y) || Double.isNaN(normal.z)) {
                    Vec3 fallback = nextDir.lengthSqr() > 0 ? nextDir : prevDir;
                    normal = fallback.normalize();
                }
            }

            Vec3 pos = model_vertexes.get(i);
            builder.putVertex(pos, normal);
        }

        Vec3 last = model_vertexes.get(n - 1);
        builder.putColor(new Color(0, 0, 0, 0));
        builder.putVertex(last, Vec3.ZERO);
    }

    @Override
    public void setVertexes(List<Vec3> vertexes) {
        this.vertexes = new ArrayList<>(vertexes);
        generateRawGeometry(false);
    }

    @Override
    public List<Vec3> getVertexes() {
        return vertexes;
    }

    public void setVertexColors(List<Color> colors) {
        this.vertexColors = new ArrayList<>(colors);
    }
    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((SimpleLineTransformer)this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }
    @Override
    public void setLineWidth(float width) {
        ((SimpleLineTransformer)this.transformer).setWidth(width);
    }

    @Override
    public float getLineWidth(boolean lerp) {
       return ((SimpleLineTransformer)this.transformer).getWidth(lerp);
    }
}

