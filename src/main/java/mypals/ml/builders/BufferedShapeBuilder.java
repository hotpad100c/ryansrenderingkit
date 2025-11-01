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
    private boolean isBuilding = false;

    private RenderMethod bufferedRenderMethod = null;

    public BufferedShapeBuilder(Matrix4f modelViewMatrix, boolean seeThrough, RenderMethod renderMethod) {
        super(modelViewMatrix, seeThrough);
        this.bufferedRenderMethod = renderMethod;
    }

    public void rebuild(RenderMethod renderMethod, Consumer<BufferedShapeBuilder> builder) {
        start(renderMethod);
        push(builder);
        end();
    }

    public void start(RenderMethod renderMethod) {
        if(this.vertexBuffer != null){
            close();
        }
        this.vertexBuffer = new VertexBuffer(GlUsage.DYNAMIC_WRITE);
        begin(renderMethod);
        this.bufferedRenderMethod = renderMethod;
        isBuilding = true;
    }

    public void push(Consumer<BufferedShapeBuilder> builder) {
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
        if (vertexBuffer == null || bufferedRenderMethod == null) {
            return;
        }

        this.vertexBuffer.bind();
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().translate(
                (float) -cameraPos.x,
                (float) -cameraPos.y,
                (float) -cameraPos.z
        );

        setUpRendererSystem(null);
        RenderSystem.setShader(bufferedRenderMethod.shader());

        this.vertexBuffer.draw(RenderSystem.getModelViewStack(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();

        restoreRendererSystem();
        RenderSystem.getModelViewStack().popMatrix();
    }
}