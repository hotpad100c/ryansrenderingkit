package mypals.ml.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
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
            setUpRendererSystem(shape);
            BufferUploader.drawWithShader(builtBuffer);
            restoreRendererSystem();
        }
    }
}