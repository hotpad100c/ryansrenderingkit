package mypals.ml.builders;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.render.RenderMethod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class BufferedShapeBuilder extends ShapeBuilder {
    private VertexBuffer vertexBuffer;
    private boolean seeThrough;
    private boolean isBuilding = false;

    public BufferedShapeBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix);
        this.seeThrough = seeThrough;
    }

    public void start(RenderMethod renderMethod) {
        this.vertexBuffer = new VertexBuffer(GlUsage.DYNAMIC_WRITE);
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        isBuilding = true;
    }

    public void push(Consumer<ShapeBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        }
    }

    public void end() {
        if (!isBuilding) {
            return;
        }

        this.vertexBuffer.bind();
        BuiltBuffer builtBuffer = this.getBufferBuilder().endNullable();
        this.bufferBuilder = null;
        if (builtBuffer == null) {
            isBuilding = false;
            return;
        }

        this.vertexBuffer.upload(builtBuffer);
        VertexBuffer.unbind();
        isBuilding = false;
    }

    public void close() {
        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
        isBuilding = false;
    }

    public void draw(Vec3d cameraPos) {
        if (vertexBuffer == null) {
            return;
        }

        if (seeThrough) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }

        this.vertexBuffer.bind();
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().translate(
                (float) -cameraPos.x,
                (float) -cameraPos.y,
                (float) -cameraPos.z
        );

        MinecraftClient.getInstance().worldRenderer.getEntityOutlinesFramebuffer().beginWrite(false);

        this.vertexBuffer.draw(RenderSystem.getModelViewStack(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());

        VertexBuffer.unbind();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.enableDepthTest();
    }
}