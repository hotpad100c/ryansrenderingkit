package mypals.ml.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
//? if > 1.20.1 {
import mypals.ml.interfaces.MeshDataExt;
//?}
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class ImmediateVertexBuilder extends VertexBuilder {

    public ImmediateVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix, seeThrough);
    }

    public void draw(Shape shape, Consumer<VertexBuilder> vertexBuilderConsumer, RenderMethod renderMethod) {
        begin(renderMethod);

        //? if < 1.21.5 {
        RenderSystem.setShader(renderMethod.shader());
        //?}
        vertexBuilderConsumer.accept(this);

        if (this.getBufferBuilder().vertices == 0) {
            //? if <= 1.20.1 {
            /*this.getBufferBuilder().endOrDiscardIfEmpty();
            this.bufferBuilder=null;
            *///?}
            return;
        }
        //? if > 1.20.1 {
        MeshData builtBuffer = this.getBufferBuilder().build();
         //?} else {
        /*BufferBuilder.RenderedBuffer builtBuffer = this.getBufferBuilder().end();
        *///?}
        if (builtBuffer != null) {
            //? if > 1.20.1 {

            ByteBufferBuilder byteBufferBuilder = null;

            if (shape.baseColor.getAlpha() < 255 && renderMethod.mode() == VertexFormat.Mode.TRIANGLES) {
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
            setUpRendererSystem(shape);

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
    }
}