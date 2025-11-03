package mypals.ml.builders.vertexBuilders;

import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import mypals.ml.render.RenderMethod;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class BufferedVertexBuilder extends VertexBuilder {
    private VertexBuffer vertexBuffer;
    private boolean isBuilding = false;

    private RenderMethod bufferedRenderMethod;

    public BufferedVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough, RenderMethod renderMethod) {
        super(modelViewMatrix, seeThrough);
        this.bufferedRenderMethod = renderMethod;
    }

    public void rebuild(RenderMethod renderMethod, Consumer<BufferedVertexBuilder> builder) {
        start(renderMethod);
        push(builder);
        end();
    }

    public void start(RenderMethod renderMethod) {
        if(this.vertexBuffer != null){
            close();
        }
        this.vertexBuffer = new VertexBuffer(BufferUsage.DYNAMIC_WRITE);
        begin(renderMethod);
        this.bufferedRenderMethod = renderMethod;
        isBuilding = true;
    }

    public void push(Consumer<BufferedVertexBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        }
    }

    public void end() {
        if (!isBuilding) {
            return;
        }

        this.vertexBuffer.bind();
        MeshData builtBuffer = this.getBufferBuilder().build();
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

    public void draw(Vec3 cameraPos) {
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

        this.vertexBuffer.drawWithShader(RenderSystem.getModelViewStack(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();

        restoreRendererSystem();
        RenderSystem.getModelViewStack().popMatrix();
    }
}