package ml.mypals.ryansrenderingkit.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
//? if >1.20.6 {
import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
//?}
import ml.mypals.ryansrenderingkit.render.RenderMethod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

//? >= 26.2 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

import java.util.function.Consumer;

public class BatchVertexBuilder extends VertexBuilder {
    public BatchVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix, seeThrough);
    }

    //? if <26.2 {
    /*private boolean isBuilding = false;

    public void beginBatch(RenderMethod renderMethod) {
        begin(renderMethod);
        //? if < 1.21.5 {
        /^RenderSystem.setShader(renderMethod.shader());
        ^///?}
        isBuilding = true;
    }

    public void push(Consumer<BatchVertexBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        } else {
            throw new IllegalStateException("BatchShapeBuilder is not building. Call beginBatch() before pushing shapes.");
        }
    }

    public void draw(Consumer<BatchVertexBuilder> builder, RenderMethod renderMethod) {
        beginBatch(renderMethod);
        push(builder);
        drawBatch(renderMethod);
    }

    public void drawBatch(RenderMethod renderMethod) {
        if (!isBuilding || this.getBufferBuilder().vertices == 0) {
            //? if <=1.20.6 && >1.18.2 {
            /^this.getBufferBuilder().endOrDiscardIfEmpty();
            this.bufferBuilder=null;
            ^///?}
            //? if <=1.18.2 {
            /^this.getBufferBuilder().endVertex();
            this.bufferBuilder=null;
            ^///?}
            return;
        }

        //? if > 1.20.6 {
        MeshData builtBuffer = this.getBufferBuilder().build();
        //?} else if > 1.18.2 {
        /^BufferBuilder.RenderedBuffer builtBuffer = this.getBufferBuilder().end();
        ^///?}
        //? if >1.18.2 {
        if (builtBuffer != null) {
        //?} else {
        /^if (this.getBufferBuilder().vertices != 0) {
        ^///?}
            //? if >1.21.1 {
            ByteBufferBuilder byteBufferBuilder = null;
            if (renderMethod.mode() == VertexFormat.Mode.TRIANGLES) {
                int vertexCount = builtBuffer.drawState().vertexCount();
                int bufferSize = vertexCount * Integer.BYTES;
                byteBufferBuilder = new ByteBufferBuilder(bufferSize);
                ((MeshDataExt) builtBuffer).ryansrenderingkit$sortTriangles(
                        byteBufferBuilder,
                        RenderSystem.getProjectionType().vertexSorting()
                );
            }
            //?}
            //? if >1.20.6 && <=1.21.1 {
            /^ByteBufferBuilder byteBufferBuilder = null;
            if (renderMethod.mode() == VertexFormat.Mode.TRIANGLES) {
                int vertexCount = builtBuffer.drawState().vertexCount();
                int bufferSize = vertexCount * Integer.BYTES;
                byteBufferBuilder = new ByteBufferBuilder(bufferSize);
                ((MeshDataExt) builtBuffer).ryansrenderingkit$sortTriangles(
                        byteBufferBuilder,
                        RenderSystem.getVertexSorting()
                );
            }
            ^///?}
            setUpRendererSystem(null);

            //? if >= 1.21.5 {
            if(seeThrough){
                renderMethod.seeThroughType().draw(builtBuffer);
            }else {
                renderMethod.normalRenderType().draw(builtBuffer);
            }
            //?} else if > 1.18.2 {
            /^BufferUploader.drawWithShader(builtBuffer);
            ^///?} else {
            /^Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            bufferBuilder.setQuadSortOrigin((float) camera.getPosition().x(), (float) camera.getPosition().y(), (float) camera.getPosition().z());
            bufferBuilder.end();
            BufferUploader.end(bufferBuilder);
            ^///?}

            //? if > 1.20.6 && < 1.21.5 {
            /^if (byteBufferBuilder != null) {
                byteBufferBuilder.close();
            }
            builtBuffer.close();
            ^///?}
            restoreRendererSystem();
            bufferBuilder = null;
        }

        isBuilding = false;
    }
    *///?}

    //? >= 26.2 {
    public void submitCustom(PoseStack poseStack, RenderMethod renderMethod, boolean seeThrough,
                             SubmitNodeStorage storage, Consumer<BatchVertexBuilder> builder) {
        RenderType renderType = seeThrough ? renderMethod.seeThroughType() : renderMethod.normalRenderType();
        storage.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            setVertexConsumer(vertexConsumer);
            builder.accept(this);
            setVertexConsumer(null);
        });
    }
    //?}
}