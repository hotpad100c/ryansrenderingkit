package mypals.ml.builders.vertexBuilders;
//? if >1.21.1 {
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
//?}

//? if >1.20.1 {

import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.vertex.*;
import mypals.ml.interfaces.MeshDataExt;
//?}
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
//? if <1.21.5 {
import com.mojang.blaze3d.vertex.VertexBuffer;
//?} else {
/*import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.GpuTexture;
*///?}
import mypals.ml.render.RenderMethod;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;

public class BufferedVertexBuilder extends VertexBuilder {
    //? >=1.21.5 {
    /*private GpuBuffer vertexBuffer;
    private int indexCount = 0;
    private GpuBuffer indexBuffer;
    *///?} else {
    private VertexBuffer vertexBuffer;
    //?}
    private boolean isBuilding = false;

    private RenderMethod bufferedRenderMethod;

    public BufferedVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough, RenderMethod renderMethod) {
        super(modelViewMatrix, seeThrough);
        this.bufferedRenderMethod = renderMethod;
    }

    public void rebuild(RenderMethod renderMethod, Consumer<BufferedVertexBuilder> builder) {
        start(renderMethod);
        push(builder);
        //flushTransparent();
        end(renderMethod);
    }

    public void start(RenderMethod renderMethod) {
        if (this.vertexBuffer != null/*? >= 1.21.5 {*//*|| indexBuffer != null *//*?}*/) {
            close();
        }

        //? < 1.21.5 {
        this.vertexBuffer = new VertexBuffer(
            //? if >1.21.1 {
            BufferUsage.DYNAMIC_WRITE
            //?} else
            /*VertexBuffer.Usage.DYNAMIC*/
            );
        //?}


        begin(renderMethod);
        this.bufferedRenderMethod = renderMethod;
        isBuilding = true;
    }

    public void push(Consumer<BufferedVertexBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        }
    }

    public void end(RenderMethod renderMethod) {

        if (!isBuilding || this.getBufferBuilder().vertices == 0) {
            //? if <= 1.20.1 {
            /*this.getBufferBuilder().endOrDiscardIfEmpty();
            *///?}
            isBuilding = false;
            this.bufferBuilder=null;
            close();
            return;
        }

        //? if > 1.20.1 {
        MeshData builtBuffer = this.getBufferBuilder().build();
        //?} else {
        /*BufferBuilder.RenderedBuffer builtBuffer = this.getBufferBuilder().end();
         *///?}
        //? if >= 1.21.5 {

        /*GpuDevice gpuDevice = RenderSystem.getDevice();
        CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
        if(this.vertexBuffer == null || this.vertexBuffer.isClosed()) {
            builtBuffer.vertexBuffer();
            this.vertexBuffer = gpuDevice.createBuffer(() -> "Vertex buffer for " + String.valueOf(this),
                    BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, builtBuffer.vertexBuffer());
            commandEncoder.writeToBuffer(vertexBuffer, builtBuffer.vertexBuffer(), 0);
        }


        indexCount = builtBuffer.drawState().indexCount();

        if(this.indexBuffer == null || this.indexBuffer.isClosed()) {
           // VertexFormat.IndexType indexType;
            if(builtBuffer.indexBuffer() == null) {
                RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer =
                        RenderSystem.getSequentialBuffer(bufferedRenderMethod.mode());
                this.indexBuffer = autoStorageIndexBuffer.getBuffer(builtBuffer.drawState().indexCount());
                //indexType = autoStorageIndexBuffer.type();
            } else{
                this.indexBuffer = gpuDevice.createBuffer(() -> "Index buffer for " + String.valueOf(this),
                        BufferType.INDICES, BufferUsage.DYNAMIC_WRITE, builtBuffer.indexBuffer());
                //indexType = builtBuffer.drawState().indexType();
            }
            //commandEncoder.writeToBuffer(indexBuffer, builtBuffer.indexBuffer(), 0);
        }

        *///?}

        //? if < 1.21.5 {
        this.vertexBuffer.bind();
        //?}



        if (builtBuffer == null) {
            isBuilding = false;
            return;
        }
        //? if > 1.20.1 && < 1.21.5 {
        ByteBufferBuilder byteBufferBuilder = null;
        if (renderMethod.mode() == VertexFormat.Mode.TRIANGLES) {
            int vertexCount = builtBuffer.drawState().vertexCount();
            int bufferSize = vertexCount * Integer.BYTES;
            byteBufferBuilder = new ByteBufferBuilder(bufferSize);
            ((MeshDataExt) builtBuffer).ryansrenderingkit$sortTriangles(
                    byteBufferBuilder,
                    //? if >1.21.1 {
                    RenderSystem.getProjectionType().vertexSorting()
                    //?} else
                    /*RenderSystem.getVertexSorting()*/
            );
        }
        //?}




        //? if < 1.21.5 {
        this.vertexBuffer.upload(builtBuffer);
        //?}
        //? if > 1.20.1 && < 1.21.5 {
        
        if (byteBufferBuilder != null) {
            byteBufferBuilder.close();
        }
        builtBuffer.close();
        //?}

        //? if < 1.21.5 {
        VertexBuffer.unbind();
        //?}
        isBuilding = false;
        bufferBuilder = null;
    }

    public void close() {

        //? if >= 1.21.5 {
            /*if (vertexBuffer != null) {
                //vertexBuffer.close();
                vertexBuffer = null;
            }
            if (indexBuffer != null) {
                //indexBuffer.close();
                indexBuffer = null;
            }
        *///?} else {
        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
        //?}


        isBuilding = false;
    }

    public void draw(Vec3 cameraPos) {
        if (vertexBuffer == null/*? >= 1.21.5 {*//*|| indexBuffer == null *//*?}*/ || bufferedRenderMethod == null) {
            return;
        }


        //? if < 1.21.5 {
        this.vertexBuffer.bind();
        //?}

        //? if > 1.20.1 {
        RenderSystem.getModelViewStack().pushMatrix();
        //?} else
        /*RenderSystem.getModelViewStack().pushPose();*/

        RenderSystem.getModelViewStack().translate(
                (float) -cameraPos.x,
                (float) -cameraPos.y,
                (float) -cameraPos.z
        );
        setUpRendererSystem(null);

        //? if < 1.21.5 {
        RenderSystem.setShader(bufferedRenderMethod.shader());
        //?}

        //? if < 1.21.5 {
        this.vertexBuffer.drawWithShader(
                //? if >1.20.1 {
                RenderSystem.getModelViewStack()
                //?} else
                /*RenderSystem.getModelViewMatrix()*/
                , RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
        //?} else {

        /*RenderTarget renderTarget = bufferedRenderMethod.renderLayer()
                .getRenderTarget();
        try (RenderPass renderPass =
                     RenderSystem.getDevice().createCommandEncoder()
                             .createRenderPass(renderTarget.getColorTexture(),
                                     OptionalInt.empty(),
                                     renderTarget.useDepth ? renderTarget.getDepthTexture()
                                             : null, OptionalDouble.empty())) {

            renderPass.setPipeline(bufferedRenderMethod.renderLayer().getRenderPipeline());
            renderPass.setVertexBuffer(0, vertexBuffer);
            if (RenderSystem.SCISSOR_STATE.isEnabled()) {
                renderPass.enableScissor(RenderSystem.SCISSOR_STATE);
            }

            RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer
                    = RenderSystem.getSequentialBuffer(bufferedRenderMethod.mode());

            renderPass.setIndexBuffer(indexBuffer, autoStorageIndexBuffer.type());
            renderPass.drawIndexed(0, indexCount);
        }

        *///?}
        restoreRendererSystem();

        //? if > 1.20.1 {
        RenderSystem.getModelViewStack().popMatrix();
        //?} else
        /*RenderSystem.getModelViewStack().popPose();*/
    }
}