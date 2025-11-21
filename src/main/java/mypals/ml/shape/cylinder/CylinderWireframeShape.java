package mypals.ml.shape.cylinder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.basics.tags.DrawableLine;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static mypals.ml.Helpers.createViewMatrix;

public class CylinderWireframeShape extends CylinderShape implements DrawableLine, LineLikeShape {

    public CylinderWireframeShape(RenderingType type, Consumer<CylinderWireframeTransformer> transform,
                                  CircleAxis circleAxis, Vec3 center, int segments,
                                  float radius, float height, float lineWidth, Color color, boolean seeThrough) {
        super(type, seeThrough);
        this.baseColor = color;
        this.transformer = new CylinderWireframeTransformer(this, segments, radius, height, lineWidth,center);
        this.transformFunction = t -> transform.accept((CylinderWireframeTransformer) this.transformer);

        this.setAxis(circleAxis);
        this.setWorldPosition(center);

        syncLastToTarget();
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        generateCylinderVertices(lerp);

        List<Integer> indices = new ArrayList<>();
        int segments = getSegments(lerp);

        int bottomStart = 0;
        int topStart = segments;

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(bottomStart + i);
            indices.add(bottomStart + next);
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(topStart + i);
            indices.add(topStart + next);
        }

        for (int i = 0; i < segments; i++) {
            indices.add(bottomStart + i);
            indices.add(topStart + i);
        }

        this.indexBuffer = indices.stream().mapToInt(Integer::intValue).toArray();
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
        builder.putColor(this.color);

        for (int i = 0; i < indexBuffer.length; i += 2) {
            Vec3 a = model_vertexes.get(indexBuffer[i]);
            Vec3 b = model_vertexes.get(indexBuffer[i + 1]);
            addLineSegment(builder, a, b);
        }
    }

    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((CylinderWireframeTransformer)this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }
    @Override
    public void setLineWidth(float width) {
        ((CylinderWireframeTransformer)this.transformer).setWidth(width);
    }
    @Override
    public float getLineWidth(boolean lerp) {
        return ((CylinderWireframeTransformer)this.transformer).getWidth(lerp);
    }

    public static class CylinderWireframeTransformer extends CylinderTransformer {
        public LineModelInfo lineModelInfo;

        public CylinderWireframeTransformer(Shape managedShape, int seg, float rad, float height, float lineWidth,Vec3 vec3) {
            super(managedShape,seg,rad,height,vec3);
            lineModelInfo = new LineModelInfo(lineWidth);
        }

        public void setWidth(float width) { lineModelInfo.setWidth(width); }
        public float getWidth(boolean lerp) { return lineModelInfo.getWidth(lerp); }

        @Override
        public void syncLastToTarget() {
            lineModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }
        @Override
        public boolean asyncModelInfo(){
            return super.asyncModelInfo() || lineModelInfo.async();
        }
    }
}

