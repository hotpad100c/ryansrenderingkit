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

    public void draw(Shape shape, Consumer<VertexBuilder> builder, RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        builder.accept(this);

        MeshData builtBuffer = this.getBufferBuilder().build();

        if(builtBuffer!=null){
            ByteBufferBuilder builder1 = null;

            if(shape.baseColor.getAlpha() < 255 && renderMethod.mode() == VertexFormat.Mode.TRIANGLES){
                builder1 = ((MeshDataExt)builtBuffer).ryansrenderingkit$sortTriangles(RenderSystem.getProjectionType().vertexSorting());
            }
            setUpRendererSystem(shape);
            BufferUploader.drawWithShader(builtBuffer);
            if(builder1 != null)builder1.close();
            builtBuffer.close();
            restoreRendererSystem();
        }
    }
}