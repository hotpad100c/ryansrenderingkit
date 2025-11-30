package mypals.ml.shape.model;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.basics.core.LineLikeShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static mypals.ml.utils.Helpers.createViewMatrix;

public class ObjModelShapeOutline extends ObjModelShape implements LineLikeShape {

    public float lineWidth;

    public ObjModelShapeOutline(RenderingType type,
                                Consumer<SimpleLineTransformer> transform,
                                ResourceLocation resourceLocation,
                                Vec3 center,
                                float lineWidth,
                                Color color,
                                boolean seeThrough) {
        super(type, (d) -> {}, resourceLocation, center, color, seeThrough);

        this.transformer = new SimpleLineTransformer(this,lineWidth,center);
        this.transformFunction = (t) -> transform.accept((SimpleLineTransformer) this.transformer);

        this.lineWidth = lineWidth;
        this.baseColor = color;

        ((SimpleLineTransformer) this.transformer).setWidth(this.lineWidth);
        this.transformer.setShapeWorldPivot(center);

        syncLastToTarget();
    }

    @Override
    public void setLineWidth(float width) {
        this.lineWidth = width;
        if (this.transformer instanceof SimpleLineTransformer slt) {
            slt.setWidth(width);
        }
    }

    @Override
    public float getLineWidth(boolean lerp) {
       return  ((SimpleLineTransformer)this.transformer).getWidth(lerp);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        super.generateRawGeometry(lerp);
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
        if (model_vertexes.isEmpty() || indexBuffer == null || indexBuffer.length < 3)
            return;

        RenderSystem.lineWidth(this.lineWidth);
        builder.putColor(this.baseColor);

        for (int[] face : model.faces) {
            int n = face.length;
            if (n < 2) continue;

            for (int i = 0; i < n; i++) {
                Vec3 v0 = model_vertexes.get(face[i]);
                Vec3 v1 = model_vertexes.get(face[(i + 1) % n]);

                addLineSegment(builder, v0, v1);
            }
        }

    }
}

