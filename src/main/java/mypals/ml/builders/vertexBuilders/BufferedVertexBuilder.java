package mypals.ml.builders.vertexBuilders;
import com.mojang.blaze3d.systems.RenderSystem;

//? if >1.20.6 {
/*import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.vertex.*;
*///?}

//? if >1.21.1 && <1.21.6 {
/*import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
*///?}

//? if <=1.21.4 {
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
 //?} else {
/*import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
*///?}

//? if >1.21.5 {
/*import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.textures.GpuTextureView;
*///?}
import com.mojang.math.Axis;
import mypals.ml.render.RenderMethod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import mypals.ml.interfaces.MeshDataExt;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
            /*BufferUsage.DYNAMIC_WRITE
            *///?} else
            VertexBuffer.Usage.DYNAMIC
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
            //? if <= 1.20.6 {
            this.getBufferBuilder().endOrDiscardIfEmpty();
            //?}
            isBuilding = false;
            this.bufferBuilder=null;
            close();
            return;
        }

        //? if > 1.20.6 {
        /*MeshData builtBuffer = this.getBufferBuilder().build();
        *///?} else {
        BufferBuilder.RenderedBuffer builtBuffer = this.getBufferBuilder().end();
         //?}
        //? if >= 1.21.5 {

        /*GpuDevice gpuDevice = RenderSystem.getDevice();
        CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
        if(this.vertexBuffer == null || this.vertexBuffer.isClosed()) {
            builtBuffer.vertexBuffer();
            //? if < 1.21.6 {
            this.vertexBuffer = gpuDevice.createBuffer(() -> "Vertex buffer for " + String.valueOf(this),
                    BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, builtBuffer.vertexBuffer());
            commandEncoder.writeToBuffer(vertexBuffer, builtBuffer.vertexBuffer(), 0);
            //?} else {
            /^this.vertexBuffer = gpuDevice.createBuffer(() -> "Vertex buffer for " + String.valueOf(this),
                    40, builtBuffer.vertexBuffer());
            commandEncoder.writeToBuffer(vertexBuffer.slice(), builtBuffer.vertexBuffer());

            ^///?}
        }


        indexCount = builtBuffer.drawState().indexCount();

        if(this.indexBuffer == null || this.indexBuffer.isClosed()) {
            if(builtBuffer.indexBuffer() == null) {
                RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer =
                        RenderSystem.getSequentialBuffer(bufferedRenderMethod.mode());
                this.indexBuffer = autoStorageIndexBuffer.getBuffer(builtBuffer.drawState().indexCount());
            } else{
                //? <1.21.6 {
                this.indexBuffer = gpuDevice.createBuffer(() -> "Index buffer for " + String.valueOf(this),
                        BufferType.INDICES, BufferUsage.DYNAMIC_WRITE, builtBuffer.indexBuffer());
                //?} else {
                /^this.indexBuffer = gpuDevice.createBuffer(() -> "Index buffer for " + String.valueOf(this),
                        72, builtBuffer.indexBuffer());
                ^///?}
            }
        }

        *///?}

        //? if < 1.21.5 {
        this.vertexBuffer.bind();
        //?}



        if (builtBuffer == null) {
            isBuilding = false;
            return;
        }
        //? if > 1.20.6 && < 1.21.5 {
        /*ByteBufferBuilder byteBufferBuilder = null;
        if (renderMethod.mode() == VertexFormat.Mode.TRIANGLES) {
            int vertexCount = builtBuffer.drawState().vertexCount();
            int bufferSize = vertexCount * Integer.BYTES;
            byteBufferBuilder = new ByteBufferBuilder(bufferSize);
            ((MeshDataExt) builtBuffer).ryansrenderingkit$sortTriangles(
                    byteBufferBuilder,
                    //? if >1.21.1 {
                    /^RenderSystem.getProjectionType().vertexSorting()
                    ^///?} else
                    RenderSystem.getVertexSorting()
            );
        }
        *///?}




        //? if < 1.21.5 {
        this.vertexBuffer.upload(builtBuffer);
        //?}
        //? if > 1.20.6 && < 1.21.5 {
        
        /*if (byteBufferBuilder != null) {
            byteBufferBuilder.close();
        }
        builtBuffer.close();
        *///?}

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

        //? if > 1.20.4 {
        /*RenderSystem.getModelViewStack().pushMatrix();
        *///?} else {
        RenderSystem.getModelViewStack().pushPose();
        //?}

        //? if <1.21 {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        RenderSystem.getModelViewStack()./*? <1.20.6 {*/mulPose/*?} else {*//*rotate*//*?}*/(Axis.XP.rotationDegrees(camera.getXRot()));
        RenderSystem.getModelViewStack()./*? <1.20.6 {*/mulPose/*?} else {*//*rotate*//*?}*/(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
        //?}
        RenderSystem.getModelViewStack().translate(
                (float) -cameraPos.x,
                (float) -cameraPos.y,
                (float) -cameraPos.z
        );

        //? if < 1.21 {
        RenderSystem.applyModelViewMatrix();
        //?}

        setUpRendererSystem(null);

        //? if < 1.21.5 {
        RenderSystem.setShader(bufferedRenderMethod.shader());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //?}

        //? if <= 1.21.4 {

        this.vertexBuffer.drawWithShader(
                //? if >1.20.6 {
                /*RenderSystem.getModelViewStack()
                *///?} else
                RenderSystem.getModelViewMatrix()
                ,RenderSystem.getProjectionMatrix(),
                 RenderSystem.getShader()
                );

        VertexBuffer.unbind();
        //?} else if <= 1.21.5 {

        /*RenderTarget renderTarget = bufferedRenderMethod.renderType().getRenderTarget();
        try (RenderPass renderPass =
                     RenderSystem.getDevice().createCommandEncoder()
                             .createRenderPass(renderTarget.getColorTexture(),
                                     OptionalInt.empty(),
                                     renderTarget.useDepth ? renderTarget.getDepthTexture()
                                             : null, OptionalDouble.empty())) {

            renderPass.setPipeline(bufferedRenderMethod.renderType().getRenderPipeline());
            renderPass.setVertexBuffer(0, vertexBuffer);
            if (RenderSystem.SCISSOR_STATE.isEnabled()) {
                renderPass.enableScissor(RenderSystem.SCISSOR_STATE);
            }

            RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer
                    = RenderSystem.getSequentialBuffer(bufferedRenderMethod.mode());

            renderPass.setIndexBuffer(indexBuffer, autoStorageIndexBuffer.type());
            renderPass.drawIndexed(0, indexCount);
        }

        *///?} else {
        /*GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), RenderSystem.getTextureMatrix(), RenderSystem.getShaderLineWidth());
        var state = bufferedRenderMethod.renderType().state;
        RenderTarget renderTarget = state.outputState.getRenderTarget();
        GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : renderTarget.getColorTextureView();
        GpuTextureView gpuTextureView2 = renderTarget.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : renderTarget.getDepthTextureView()) : null;

        try (RenderPass renderPass =
            RenderSystem.getDevice().createCommandEncoder()
                 .createRenderPass(()->"RenderPass_"
                 +bufferedRenderMethod.renderType().toString()
                 +this.getClass(),
                 gpuTextureView,
                 OptionalInt.empty(),
                 gpuTextureView2,
                 OptionalDouble.empty()
                 )
        ){
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setPipeline(bufferedRenderMethod.renderType().renderPipeline);
            renderPass.setVertexBuffer(0, vertexBuffer);
            ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
            if (scissorState.enabled()) {
                renderPass.enableScissor(scissorState.x(), scissorState.y(), scissorState.width(), scissorState.height());
            }

            RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer
                    = RenderSystem.getSequentialBuffer(bufferedRenderMethod.mode());

            renderPass.setIndexBuffer(indexBuffer, autoStorageIndexBuffer.type());
            renderPass.drawIndexed(0,0, indexCount,1);

        }
        *///?}
        restoreRendererSystem();

        //? if > 1.20.4 {
        /*RenderSystem.getModelViewStack().popMatrix();
        *///?} else {
        RenderSystem.getModelViewStack().popPose();
        //?}

        //? if < 1.21 {
        RenderSystem.applyModelViewMatrix();
        //?}
    }
}