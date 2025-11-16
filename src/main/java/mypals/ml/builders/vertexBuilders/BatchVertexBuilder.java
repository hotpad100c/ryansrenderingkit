package mypals.ml.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import mypals.ml.interfaces.MeshDataExt;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class BatchVertexBuilder extends VertexBuilder {
    private boolean isBuilding = false;
    public BatchVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix,seeThrough);
    }

    public void beginBatch(RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        isBuilding = true;
    }

    public void push(Consumer<VertexBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        }else{
            throw new IllegalStateException("BatchShapeBuilder is not building. Call beginBatch() before pushing shapes.");
        }
    }

    public void draw(Shape shape, Consumer<VertexBuilder> builder, RenderMethod renderMethod) {
        beginBatch(renderMethod);
        push(builder);
        drawBatch(renderMethod);
    }

    public void drawBatch(RenderMethod renderMethod) {
        if (!isBuilding || this.getBufferBuilder().vertices == 0) {
            return;
        }
        //flushTransparent();

        MeshData builtBuffer = this.getBufferBuilder().build();
        if(builtBuffer!=null){
            ByteBufferBuilder byteBufferBuilder = null;
            if(renderMethod.mode() == VertexFormat.Mode.TRIANGLES){
                int vertexCount = builtBuffer.drawState().vertexCount();
                int bufferSize = vertexCount * Integer.BYTES;
                byteBufferBuilder = new ByteBufferBuilder(bufferSize);
                ((MeshDataExt)builtBuffer).ryansrenderingkit$sortTriangles(byteBufferBuilder, RenderSystem.getProjectionType().vertexSorting());
            }
            setUpRendererSystem(null);
            BufferUploader.drawWithShader(builtBuffer);
            if(byteBufferBuilder != null){
                byteBufferBuilder.close();
            }
            builtBuffer.close();
            restoreRendererSystem();
        }


        isBuilding = false;
    }
}