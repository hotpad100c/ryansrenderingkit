package ml.mypals.ryansrenderingkit.builders.vertexBuilders;
import com.mojang.blaze3d.systems.RenderSystem;

//? if >1.20.6 {

import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.vertex.*;
    //? if < 1.21.5 {
    /*import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
    *///?}
//?} else {
/*import com.mojang.blaze3d.vertex.BufferUploader;
//? if <=1.18.2 {
/^import com.mojang.math.Vector3f;
^///?} else {
import com.mojang.math.Axis;
//?}
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
*///?}

//? if >1.21.1 && <1.21.6 {
/*import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
*///?}

//? if <=1.21.4 {
/*import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
 *///?} else {
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
//?}

//? if >1.21.5 {
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.textures.GpuTextureView;
//? if <1.19.4 {
/*import com.mojang.math.Vector3f;
*///?} else {
import org.joml.Vector3f;
//?}
import org.joml.Vector4f;
//?}
import ml.mypals.ryansrenderingkit.render.RenderMethod;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;

import static ml.mypals.ryansrenderingkit.RyansRenderingKit.LOGGER;

public class BufferedVertexBuilder extends VertexBuilder {
    //? >=1.21.5 {
    private GpuBuffer vertexBuffer;
    private int indexCount = 0;
    private GpuBuffer indexBuffer;
    /** False when {@link #indexBuffer} is RenderSystem's shared sequential buffer, which we must not close. */
    private boolean ownsIndexBuffer = false;
    //?} else {
    /*private VertexBuffer vertexBuffer;
    *///?}
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
        if (this.vertexBuffer != null/*? >= 1.21.5 {*/|| indexBuffer != null /*?}*/) {
            close();
        }

        //? < 1.21.5 {
        /*this.vertexBuffer = new VertexBuffer(
            //? if >1.21.1 {
            BufferUsage.DYNAMIC_WRITE
            //?} else if > 1.19.4 {
            /^VertexBuffer.Usage.DYNAMIC
            ^///?}
            );
        *///?}


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
            /*this.getBufferBuilder()./^? if >1.18.2 {^/endOrDiscardIfEmpty()/^?} else {^//^endVertex()^//^?}^/;
            *///?}
            isBuilding = false;
            this.bufferBuilder=null;
            close();
            return;
        }

        //? if > 1.20.6 {
        MeshData builtBuffer = this.getBufferBuilder().build();
         //?} else if > 1.18.2 {
        /*BufferBuilder.RenderedBuffer builtBuffer = this.getBufferBuilder().end();
         *///?} else {
        //?}

        //? if >= 1.21.5 {

        GpuDevice gpuDevice = RenderSystem.getDevice();
        if(this.vertexBuffer == null || this.vertexBuffer.isClosed()) {
            //? if < 1.21.6 {
            /*try (CommandEncoder commandEncoder = gpuDevice.createCommandEncoder()) {
                this.vertexBuffer = gpuDevice.createBuffer(() -> "Vertex buffer for " + String.valueOf(this),
                        BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, builtBuffer.vertexBuffer());
                commandEncoder.writeToBuffer(vertexBuffer, builtBuffer.vertexBuffer(), 0);
            }
            *///?} else {
            this.vertexBuffer = gpuDevice.createBuffer(() -> "Vertex buffer for " + String.valueOf(this),
                    40, builtBuffer.vertexBuffer());

            //?}
        }


        indexCount = builtBuffer.drawState().indexCount();

        if(builtBuffer.indexBuffer() == null) {
            // RenderSystem's shared sequential index buffer. Re-fetched on every rebuild because it is
            // recreated whenever it has to grow -- a cached handle would name a deleted buffer. Never
            // ours to close; see releaseIndexBuffer().
            releaseIndexBuffer();
            RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer =
                    RenderSystem.getSequentialBuffer(bufferedRenderMethod.mode());
            this.indexBuffer = autoStorageIndexBuffer.getBuffer(indexCount);
            this.ownsIndexBuffer = false;
        } else if(this.indexBuffer == null || this.indexBuffer.isClosed()) {
            //? <1.21.6 {
            /*try (CommandEncoder commandEncoder = gpuDevice.createCommandEncoder()) {
                this.indexBuffer = gpuDevice.createBuffer(() -> "Index buffer for " + String.valueOf(this),
                        BufferType.INDICES, BufferUsage.DYNAMIC_WRITE, builtBuffer.indexBuffer());
                commandEncoder.writeToBuffer(indexBuffer, builtBuffer.indexBuffer(), 0);
            }
            *///?} else {
            this.indexBuffer = gpuDevice.createBuffer(() -> "Index buffer for " + String.valueOf(this),
                    72, builtBuffer.indexBuffer());
            //?}
            this.ownsIndexBuffer = true;
        }

        //?}

        //? if < 1.21.5 {
        /*this.vertexBuffer.bind();
        *///?}


        //? if >1.18.2 {
        if (builtBuffer == null) {
        //?} else {
        /*if (bufferBuilder.vertices <= 0) {
        *///?}
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


        //? if >= 1.21.5 {

        //?} else if > 1.18.2 && < 1.21.5 {
        /*this.vertexBuffer.upload(builtBuffer);
         *///?} else {
        /*//Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        //bufferBuilder.setQuadSortOrigin((float) camera.getPosition().x(), (float) camera.getPosition().y(), (float) camera.getPosition().z());
        bufferBuilder.end();
        this.vertexBuffer.upload(bufferBuilder);
        *///?}


        //? if > 1.20.6 && < 1.21.5 {
        /*if (byteBufferBuilder != null) {
            byteBufferBuilder.close();
        }
        builtBuffer.close();
        *///?}

        //? if < 1.21.5 {
        /*VertexBuffer.unbind();
        *///?}
        isBuilding = false;
        bufferBuilder = null;
    }

    public void close() {

        //? if >= 1.21.5 {
            if (vertexBuffer != null) {
                vertexBuffer.close();
                vertexBuffer = null;
            }
            releaseIndexBuffer();
        //?} else {
        /*if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
        *///?}


        isBuilding = false;
    }

    //? if >= 1.21.5 {
    /** Drops the index buffer, destroying it only when we allocated it rather than borrowed it. */
    private void releaseIndexBuffer() {
        if (indexBuffer != null) {
            if (ownsIndexBuffer) {
                indexBuffer.close();
            }
            indexBuffer = null;
            ownsIndexBuffer = false;
        }
    }
    //?}

    public void draw(Vec3 cameraPos) {
        if (vertexBuffer == null/*? >= 1.21.5 {*/|| indexBuffer == null /*?}*/ || bufferedRenderMethod == null) {
            return;
        }
        //? if < 1.21.5 {
        /*this.vertexBuffer.bind();
        *///?}

        //? if > 1.20.4 {
        RenderSystem.getModelViewStack().pushMatrix();
        //?} else {
        /*RenderSystem.getModelViewStack().pushPose();
        *///?}

        //? if <1.20.6 {
            /*Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            RenderSystem.getModelViewStack()./^? <1.20.6 {^//^mulPose^//^?} else {^/rotate/^?}^/(/^? if <=1.18.2 {^/ /^Vector3f ^//^?} else {^/Axis/^?}^/.XP.rotationDegrees(camera.getXRot()));
            RenderSystem.getModelViewStack()./^? <1.20.6 {^//^mulPose^//^?} else {^/rotate/^?}^/(/^? if <=1.18.2 {^/ /^Vector3f ^//^?} else {^/Axis/^?}^/.YP.rotationDegrees(camera.getYRot() + 180.0F));
        *///?}
        RenderSystem.getModelViewStack().translate(
                (float) -cameraPos.x,
                (float) -cameraPos.y,
                (float) -cameraPos.z
        );

        //? if < 1.21 {
        /*RenderSystem.applyModelViewMatrix();
        *///?}

        setUpRendererSystem(null);

        //? if < 1.21.5 {
        /*RenderSystem.setShader(bufferedRenderMethod.shader());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///?}

        //? if <= 1.21.4 {
        /*try {
            this.vertexBuffer.drawWithShader(
                    //? if >1.20.6 {
                    RenderSystem.getModelViewStack()
                     //?} else
                    //RenderSystem.getModelViewMatrix()
                    , RenderSystem.getProjectionMatrix(),
                    RenderSystem.getShader()
            );
        }catch (Exception e){
            LOGGER.error("Error while drawing buffered vertex builder:",e);
        }
        VertexBuffer.unbind();
        *///?} else if <= 1.21.5 {

        /*RenderTarget renderTarget = bufferedRenderMethod.normalRenderType().getRenderTarget();
        try (RenderPass renderPass =
                     RenderSystem.getDevice().createCommandEncoder()
                             .createRenderPass(renderTarget.getColorTexture(),
                                     OptionalInt.empty(),
                                     renderTarget.useDepth ? renderTarget.getDepthTexture()
                                             : null, OptionalDouble.empty())) {
            if(seeThrough){
                renderPass.setPipeline(bufferedRenderMethod.seeThroughType().getRenderPipeline());
            }else{
                renderPass.setPipeline(bufferedRenderMethod.normalRenderType().getRenderPipeline());
            }
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
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().writeTransform(
                //?if<26.2{
                /*RenderSystem.getModelViewMatrix()
                *///?}else{
                RenderSystem.getModelViewMatrixCopy()
                //?}
                , new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                new Vector3f(),
                /*? if <1.21.11 {*//*RenderSystem.getTextureMatrix(), RenderSystem.getShaderLineWidth()*//*?}else if < 26.2{*//*bufferedRenderMethod.normalRenderType().state.textureTransform.getMatrix()*//*?}else{*/bufferedRenderMethod.normalRenderType().state.textureTransform.createMatrix() /*?}*/);
        var state = bufferedRenderMethod.normalRenderType().state;
        RenderTarget renderTarget = state./*? if >=1.21.11 {*/outputTarget/*?} else {*//*outputState*//*?}*/.getRenderTarget();
        GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : renderTarget.getColorTextureView();
        GpuTextureView gpuTextureView2 = renderTarget.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : renderTarget.getDepthTextureView()) : null;

        try (RenderPass renderPass =
            RenderSystem.getDevice().createCommandEncoder()
                 .createRenderPass(()->"RenderPass_"
                 +bufferedRenderMethod.normalRenderType().toString()
                 +this.getClass(),
                 gpuTextureView,
                 Optional.empty(),
                 gpuTextureView2,
                 OptionalDouble.empty()
                 )
        ){
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            if(seeThrough){
                renderPass.setPipeline(bufferedRenderMethod.seeThroughType()./*? if >=1.21.11 {*/pipeline()/*?} else {*//*renderPipeline*//*?}*/);
            }else{
                renderPass.setPipeline(bufferedRenderMethod.normalRenderType()./*? if >=1.21.11 {*/pipeline()/*?} else {*//*renderPipeline*//*?}*/);
            }
            renderPass.setVertexBuffer(0, vertexBuffer.slice());
            ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
            if (scissorState.enabled()) {
                renderPass.enableScissor(scissorState.x(), scissorState.y(), scissorState.width(), scissorState.height());
            }

            RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer
                    = RenderSystem.getSequentialBuffer(bufferedRenderMethod.mode());

            renderPass.setIndexBuffer(indexBuffer, autoStorageIndexBuffer.type());
            renderPass.drawIndexed(indexCount,1, 0, 0, 0);

        }
        //?}
        restoreRendererSystem();

        //? if > 1.20.4 {
        RenderSystem.getModelViewStack().popMatrix();
        //?} else {
        /*RenderSystem.getModelViewStack().popPose();
        *///?}
        //? if < 1.21 {
        /*RenderSystem.applyModelViewMatrix();
        *///?}
    }
}