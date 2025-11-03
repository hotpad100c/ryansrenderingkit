package mypals.ml.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.function.Consumer;

public abstract class VertexBuilder {
    protected Matrix4f positionMatrix;
    protected BufferBuilder bufferBuilder;
    protected float a = 1f, r = 1f, g = 1f, b = 1f;
    protected boolean seeThrough = false;

    public VertexBuilder(Matrix4f modelViewMatrix) {
        this.positionMatrix = modelViewMatrix;
    }

    public VertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        this.positionMatrix = modelViewMatrix;
        this.seeThrough = seeThrough;
    }

    public Matrix4f getPositionMatrix(){
        return positionMatrix;
    }
    public void setPositionMatrix(Matrix4f modelViewMatrix) {
        this.positionMatrix = modelViewMatrix;
    }

    protected void begin(RenderMethod renderMethod) {
        this.bufferBuilder = Tesselator.getInstance().begin(renderMethod.mode(), renderMethod.format());
    }

    public BufferBuilder getBufferBuilder() {
        return this.bufferBuilder;
    }

    public void putColor(int argb) {
        this.a = ((argb >> 24) & 0xFF) / 255f;
        this.r = ((argb >> 16) & 0xFF) / 255f;
        this.g = ((argb >> 8) & 0xFF) / 255f;
        this.b = (argb & 0xFF) / 255f;
    }

    public void putColor(Color color) {
        int argb = color.getRGB();
        this.putColor(argb);
    }

    public float[] toARGB(Color color) {
        int argb = color.getRGB();
        float[] colors = new float[4];
        colors[0] = ((argb >> 24) & 0xFF) / 255f;
        colors[1] = ((argb >> 16) & 0xFF) / 255f;
        colors[2] = ((argb >> 8) & 0xFF) / 255f;
        colors[3] = (argb & 0xFF) / 255f;
        return colors;
    }

    protected boolean shouldApplyNormal(RenderMethod renderMethod) {
        return renderMethod == RenderMethod.LINES || renderMethod == RenderMethod.LINE_STRIP;
    }

    public void putVertex(Vector3f v, float r, float g, float b, float a) {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z).setColor(r, g, b, a);
    }

    public void putVertex(Vector3f v, float r, float g, float b, float a, Vector3f normal) {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z)
                .setColor(r, g, b, a)
                .setNormal(normal.x, normal.y, normal.z);
    }
    public void putVertex(Vector3f v, Color color) {
        float[] argb = toARGB(color);
        putVertex(v, argb[1], argb[2], argb[3], argb[0]);
    }

    public void putVertex(Vector3f v, Color color, Vector3f normal) {
        float[] argb = toARGB(color);
        putVertex(v, argb[1], argb[2], argb[3], argb[0], normal);
    }

    public void putVertex(Vec3 v, Color color) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z), color);
    }

    public void putVertex(Vec3 v, Color color, Vec3 normal) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z), color,
                new Vector3f((float) normal.x, (float) normal.y, (float) normal.z));
    }

    public void putVertex(Vec3 v) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z));
    }

    public void putVertex(Vec3 v, Vec3 normal) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z),
                new Vector3f((float) normal.x, (float) normal.y, (float) normal.z));
    }

    public void putVertex(Vector3f v) {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z)
                .setColor(r, g, b, a);
    }

    public void putVertex(Vector3f v, Vector3f normal) {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z)
                .setColor(r, g, b, a)
                .setNormal(normal.x, normal.y, normal.z);
    }

    public void putVertex(float x, float y, float z) {
        putVertex(new Vector3f(x, y, z));
    }

    public void putVertex(float x, float y, float z, float nx, float ny, float nz) {
        putVertex(new Vector3f(x, y, z), new Vector3f(nx, ny, nz));
    }
    public void draw(Shape shape, Consumer<VertexBuilder> builder, RenderMethod renderMethod) {
    }
    public void setUpRendererSystem(@Nullable Shape shape) {
        if ( (shape != null && shape.seeThrough) || seeThrough) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.disableCull();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(-1.0f, -1.0f);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
    public void restoreRendererSystem() {
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.lineWidth(1.0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.enableCull();
    }
}