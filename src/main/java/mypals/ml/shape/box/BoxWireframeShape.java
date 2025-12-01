package mypals.ml.shape.box;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.basics.tags.DrawableLine;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static mypals.ml.utils.Helpers.createViewMatrix;

public class BoxWireframeShape extends BoxShape implements DrawableLine {

    public final float edgeWidth;

    public BoxWireframeShape(RenderingType type,
                             Consumer<BoxTransformer> transform,
                             Vec3 vec1,
                             Vec3 vec2,
                             Color edgeColor,
                             boolean seeThrough,
                             float edgeWidth,
                             BoxConstructionType constructionType) {

        super(type, transform, vec1, vec2, edgeColor, seeThrough, constructionType);
        this.edgeWidth = Math.max(0.1f, edgeWidth);
        generateRawGeometry(false);
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

    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((LineLikeShape.SimpleLineTransformer) this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void setLineWidth(float width) {
        ((LineLikeShape.SimpleLineTransformer) this.transformer).setWidth(width);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();

        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 c = bt.getLocalPivot();
        Vec3 d = bt.getDimension(lerp);

        double hx = d.x * 0.5;
        double hy = d.y * 0.5;
        double hz = d.z * 0.5;

        Vec3 v0 = new Vec3(c.x - hx, c.y - hy, c.z - hz);
        Vec3 v1 = new Vec3(c.x + hx, c.y - hy, c.z - hz);
        Vec3 v2 = new Vec3(c.x + hx, c.y - hy, c.z + hz);
        Vec3 v3 = new Vec3(c.x - hx, c.y - hy, c.z + hz);
        Vec3 v4 = new Vec3(c.x - hx, c.y + hy, c.z - hz);
        Vec3 v5 = new Vec3(c.x + hx, c.y + hy, c.z - hz);
        Vec3 v6 = new Vec3(c.x + hx, c.y + hy, c.z + hz);
        Vec3 v7 = new Vec3(c.x - hx, c.y + hy, c.z + hz);

        model_vertexes.add(v0); // 0
        model_vertexes.add(v1); // 1
        model_vertexes.add(v2); // 2
        model_vertexes.add(v3); // 3
        model_vertexes.add(v4); // 4
        model_vertexes.add(v5); // 5
        model_vertexes.add(v6); // 6
        model_vertexes.add(v7); // 7

        indexBuffer = new int[]{
                // Bottom
                0, 1, 1, 2, 2, 3, 3, 0,
                // Top
                4, 5, 5, 6, 6, 7, 7, 4,
                // Vert
                0, 4, 1, 5, 2, 6, 3, 7
        };
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        RenderSystem.lineWidth(edgeWidth);
        builder.putColor(baseColor);

        for (int i = 0; i < indexBuffer.length; i += 2) {
            Vec3 start = model_vertexes.get(indexBuffer[i]);
            Vec3 end = model_vertexes.get(indexBuffer[i + 1]);
            addLineSegment(builder, start, end);
        }
    }

    private void addLineSegment(VertexBuilder builder, Vec3 start, Vec3 end) {
        Vec3 dir = end.subtract(start);
        dir.normalize();

        Vec3 normal = new Vec3(dir.x(), dir.y(), dir.z());

        builder.putVertex(start, normal);
        builder.putVertex(end, normal);
    }
}
