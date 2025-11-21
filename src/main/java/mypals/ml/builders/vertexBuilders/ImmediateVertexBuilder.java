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

public class ImmediateVertexBuilder extends VertexBuilder {

    public ImmediateVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix, seeThrough);
    }

    public void draw(Shape shape, Consumer<VertexBuilder> vertexBuilderConsumer, RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        vertexBuilderConsumer.accept(this);

        if(this.getBufferBuilder().vertices == 0){
            return;
        }
        MeshData meshData = this.getBufferBuilder().build();

        if(meshData!=null){
            ByteBufferBuilder byteBufferBuilder = null;

            if(shape.baseColor.getAlpha() < 255 && renderMethod.mode() == VertexFormat.Mode.TRIANGLES){
                int vertexCount = meshData.drawState().vertexCount();
                int bufferSize = vertexCount * Integer.BYTES;
                byteBufferBuilder = new ByteBufferBuilder(bufferSize);

                ((MeshDataExt)meshData).ryansrenderingkit$sortTriangles(byteBufferBuilder,RenderSystem.getProjectionType().vertexSorting());
            }
            setUpRendererSystem(shape);
            BufferUploader.drawWithShader(meshData);
            if(byteBufferBuilder != null){
                byteBufferBuilder.close();
            }
            meshData.close();
            restoreRendererSystem();
        }
    }
}