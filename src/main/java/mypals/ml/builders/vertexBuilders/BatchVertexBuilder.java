package mypals.ml.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
//? if >1.20.1 {
import mypals.ml.interfaces.MeshDataExt;
//?}
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class BatchVertexBuilder extends VertexBuilder {
    private boolean isBuilding = false;

    public BatchVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix, seeThrough);
    }

    public void beginBatch(RenderMethod renderMethod) {
        begin(renderMethod);
        //? if < 1.21.5 {
        RenderSystem.setShader(renderMethod.shader());
        //?}
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
            //? if <= 1.20.1 {
            /*this.getBufferBuilder().endOrDiscardIfEmpty();
            this.bufferBuilder=null;
            *///?}
            return;
        }
        //flushTransparent();

        //? if > 1.20.1 {
        MeshData builtBuffer = this.getBufferBuilder().build();
        //?} else {
        /*BufferBuilder.RenderedBuffer builtBuffer = this.getBufferBuilder().end();
        *///?}
        if (builtBuffer != null) {

            //? if > 1.20.1 {
            ByteBufferBuilder byteBufferBuilder = null;
            if (renderMethod.mode() == VertexFormat.Mode.TRIANGLES) {
                int vertexCount = builtBuffer.drawState().vertexCount();
                int bufferSize = vertexCount * Integer.BYTES;
                byteBufferBuilder = new ByteBufferBuilder(bufferSize);
                ((MeshDataExt) builtBuffer).ryansrenderingkit$sortTriangles(
                        byteBufferBuilder,
                        //? if >1.21.1 {
                        /*RenderSystem.getProjectionType().vertexSorting()
                        *///?} else
                        RenderSystem.getVertexSorting()
                );
            }
            //?}
            setUpRendererSystem(null);
            //? if >= 1.21.5 {
            /*renderMethod.renderLayer().draw(builtBuffer);
            *///?} else {
            BufferUploader.drawWithShader(builtBuffer);
            //?}
            //? if > 1.20.1 && < 1.21.5 {
            if (byteBufferBuilder != null) {
                byteBufferBuilder.close();
            }
            builtBuffer.close();
            //?}
            restoreRendererSystem();
            bufferBuilder = null;
        }


        isBuilding = false;
    }
}