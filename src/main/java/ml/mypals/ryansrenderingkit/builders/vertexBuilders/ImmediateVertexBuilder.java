package ml.mypals.ryansrenderingkit.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
//? if > 1.20.6 {
/*import ml.mypals.ryansrenderingkit.interfaces.MeshDataExt;
*///?}
import ml.mypals.ryansrenderingkit.render.RenderMethod;
import ml.mypals.ryansrenderingkit.shape.Shape;
import org.joml.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

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
            //? if <= 1.20.6 {
            this.getBufferBuilder()./*? if >1.18.2 {*/endOrDiscardIfEmpty()/*?} else {*//*endVertex()*//*?}*/;
            this.bufferBuilder=null;
            //?}
            return;
        }
        //? if > 1.20.6 {
        /*MeshData builtBuffer = this.getBufferBuilder().build();
         *///?} else if > 1.18.2 {
        BufferBuilder.RenderedBuffer builtBuffer = this.getBufferBuilder().end();
         //?}
        //? if >1.18.2 {
        if (builtBuffer != null) {
         //?} else {
        /*if (this.getBufferBuilder().vertices != 0) {
        *///?}
            //? if > 1.20.6 {

            /*ByteBufferBuilder byteBufferBuilder = null;

            if (shape.baseColor.getAlpha() < 255 && renderMethod.mode() == VertexFormat.Mode.TRIANGLES) {
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
            setUpRendererSystem(shape);

            //? if >= 1.21.5 {
            /*if(seeThrough){
                renderMethod.seeThroughType().draw(builtBuffer);
            }else {
                renderMethod.normalRenderType().draw(builtBuffer);
            }
            *///?} else if > 1.18.2 {
            BufferUploader.drawWithShader(builtBuffer);
             //?} else {
            /*Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            bufferBuilder.setQuadSortOrigin((float) camera.getPosition().x(), (float) camera.getPosition().y(), (float) camera.getPosition().z());
            bufferBuilder.end();
            BufferUploader.end(bufferBuilder);
            *///?}


            //? if > 1.20.6 && < 1.21.5 {
            /*if (byteBufferBuilder != null) {
                byteBufferBuilder.close();
            }
            builtBuffer.close();
            *///?}
            restoreRendererSystem();
            bufferBuilder = null;
        }
    }
}