package ml.mypals.ryansrenderingkit.builders.vertexBuilders;
//? >= 1.21.5 {
import com.mojang.blaze3d.opengl.GlStateManager;
//?}
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
//?if<26.2{
/*import com.mojang.blaze3d.vertex.Tesselator;
*///?}else{
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
//?}
import ml.mypals.ryansrenderingkit.render.RenderMethod;
import ml.mypals.ryansrenderingkit.shape.Shape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.awt.*;
import java.util.function.Consumer;

import static ml.mypals.ryansrenderingkit.utils.Helpers.convertToMojangIfNeeded;

public abstract class VertexBuilder {
    protected Matrix4f positionMatrix;
    protected BufferBuilder bufferBuilder;
    //? >= 26.2 {
    protected ByteBufferBuilder byteBufferBuilder;
    protected VertexConsumer vertexConsumer;
    //?}
    protected float a = 1f, r = 1f, g = 1f, b = 1f;
    protected boolean seeThrough = false;

    //? >= 26.2 {
    public void setVertexConsumer(VertexConsumer consumer) {
        this.vertexConsumer = consumer;
    }
    //?}

    public VertexBuilder(Matrix4f modelViewMatrix) {
        this.positionMatrix = modelViewMatrix;
    }

    public VertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        this.positionMatrix = modelViewMatrix;
        this.seeThrough = seeThrough;
    }

    public Matrix4f getPositionMatrix() {
        return positionMatrix;
    }

    public void setPositionMatrix(Matrix4f modelViewMatrix) {
        this.positionMatrix = modelViewMatrix;
    }

    protected void begin(RenderMethod renderMethod) {
        if(bufferBuilder != null) {
            return;
        }

        //? if >= 26.2 {
        // Batch/Immediate get VertexConsumer from StagedVertexBuffer (via SubmitNodeStorage)
        // Buffered still needs its own BufferBuilder for VBO rebuild
        if (this.byteBufferBuilder != null) {
            this.byteBufferBuilder.close();
        }
        this.byteBufferBuilder = new ByteBufferBuilder(1536);
        this.bufferBuilder = new BufferBuilder(this.byteBufferBuilder, renderMethod.mode(), renderMethod.format());
        //?} else if > 1.20.6 {
            /*this.bufferBuilder = Tesselator.getInstance().begin(renderMethod.mode(), renderMethod.format());
            *///?} else {
        /*this.bufferBuilder = Tesselator.getInstance().getBuilder();
        //? if <=1.18.2 {
        /^if(bufferBuilder.building()){
            return;
        }
        ^///?}
        bufferBuilder.begin(renderMethod.mode(), renderMethod.format());
        *///?}
    }

    public BufferBuilder getBufferBuilder() {
        return this.bufferBuilder;
    }

    public void putColor(int argb) {
        this.a = ((argb >> 24) & 0xFF) / 255f;
        this.r = ((argb >> 16) & 0xFF) / 255f;
        this.g = ((argb >> 8) & 0xFF) / 255f;
        this.b = (argb & 0xFF) / 255f;
    }

    public void putColor(Color color) {
        int argb = color.getRGB();
        this.putColor(argb);
    }

    public float[] toARGB(Color color) {
        int argb = color.getRGB();
        float[] colors = new float[4];
        colors[0] = ((argb >> 24) & 0xFF) / 255f;
        colors[1] = ((argb >> 16) & 0xFF) / 255f;
        colors[2] = ((argb >> 8) & 0xFF) / 255f;
        colors[3] = (argb & 0xFF) / 255f;
        return colors;
    }

    public void putVertex(Vector3f v, float r, float g, float b, float a) {
        //? if >= 26.2 {
        if (vertexConsumer != null) {
            vertexConsumer.addVertex(positionMatrix, v.x, v.y, v.z).setColor(r, g, b, a);
            return;
        }
        //?}
        //? if > 1.20.6 {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z).setColor(r, g, b, a);
        //?} else {
        /*this.bufferBuilder.vertex(convertToMojangIfNeeded(positionMatrix), v.x(), v.y(), v.z()).color(r, g, b, a).endVertex();
        *///?}
        }

    public void putVertex(Vector3f v, float r, float g, float b, float a, Vector3f normal,float width) {
        //? if >= 26.2 {
        if (vertexConsumer != null) {
            vertexConsumer.addVertex(positionMatrix, v.x, v.y, v.z)
                    .setColor(r, g, b, a)
                    .setNormal(normal.x, normal.y, normal.z)
                    //? if >=1.21.11 {
                    .setLineWidth(width)
                    //?}
                    ;
            return;
        }
        //?}
        //? if > 1.20.6 {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z)
                .setColor(r, g, b, a)
                .setNormal(normal.x, normal.y, normal.z)
                //? if >=1.21.11 {
                .setLineWidth(width)
                //?}
                ;
        //?} else {
        /*this.bufferBuilder.vertex(convertToMojangIfNeeded(positionMatrix), v.x(), v.y(), v.z())
                .color(r, g, b, a)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();
        *///?}
    }

    public void putVertex(Vector3f v, Color color) {
        float[] argb = toARGB(color);
        putVertex(v, argb[1], argb[2], argb[3], argb[0]);
    }

    public void putVertex(Vector3f v, Color color, Vector3f normal,float width) {
        float[] argb = toARGB(color);
        putVertex(v, argb[1], argb[2], argb[3], argb[0], normal, width);
    }

    public void putVertex(Vec3 v, Color color) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z), color);
    }

    public void putVertex(Vec3 v, Color color, Vec3 normal,float width) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z), color,
                new Vector3f((float) normal.x, (float) normal.y, (float) normal.z), width);
    }

    public void putVertex(Vec3 v) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z));
    }


    public void putVertex(Vec3 v, Vec3 normal,float width) {
        putVertex(new Vector3f((float) v.x, (float) v.y, (float) v.z),
                new Vector3f((float) normal.x, (float) normal.y, (float) normal.z),width);
    }


    public void putVertex(Vector3f v) {
        //? if >= 26.2 {
        if (vertexConsumer != null) {
            vertexConsumer.addVertex(positionMatrix, v.x, v.y, v.z).setColor(r, g, b, a);
            return;
        }
        //?}
        //? if > 1.20.6 {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z).setColor(r, g, b, a);
         //?} else {
        /*this.bufferBuilder.vertex(convertToMojangIfNeeded(positionMatrix), v.x(), v.y(), v.z()).color(r, g, b, a).endVertex();
        *///?}
    }

    public void putVertex(Vector3f v, Vector3f normal,float width) {
        //? if >= 26.2 {
        if (vertexConsumer != null) {
            vertexConsumer.addVertex(positionMatrix, v.x, v.y, v.z)
                    .setColor(r, g, b, a)
                    .setNormal(normal.x, normal.y, normal.z)
                    //? if >=1.21.11 {
                    .setLineWidth(width)
                    //?}
                    ;
            return;
        }
        //?}
        //? if > 1.20.6 {
        this.bufferBuilder.addVertex(positionMatrix, v.x, v.y, v.z)
                .setColor(r, g, b, a)
                .setNormal(normal.x, normal.y, normal.z)
                //? if >=1.21.11 {
                .setLineWidth(width)
                //?}
                ;
        //?} else {
        /*this.bufferBuilder.vertex(convertToMojangIfNeeded(positionMatrix), v.x(), v.y(), v.z())
                .color(r, g, b, a)
                .normal(normal.x(), normal.y(), normal.z())
                .endVertex();
        *///?}
    }

    public void putVertex(float x, float y, float z) {
        putVertex(new Vector3f(x, y, z));
    }

    public void putVertex(float x, float y, float z, float nx, float ny, float nz,float width) {
        putVertex(new Vector3f(x, y, z), new Vector3f(nx, ny, nz),width);
    }

    public void draw(Shape shape, Consumer<VertexBuilder> builder, RenderMethod renderMethod) {
    }

    public void setUpRendererSystem(@Nullable Shape shape) {
    //?if<26.2{
        /*if ((shape != null && shape.seeThrough) || seeThrough) {
                //?if >=1.21.5{
                GlStateManager._disableDepthTest();
                //?} else {
                 /^RenderSystem.disableDepthTest();
                ^///?}
        } else {
                //?if >=1.21.5{
                GlStateManager._enableDepthTest();
                //?} else {
                 /^RenderSystem.enableDepthTest();
                ^///?}
        }
        //?if >=1.21.5{
        GlStateManager._disableCull();
        GlStateManager._enablePolygonOffset();
        GlStateManager._polygonOffset(-1.0f, -1.0f);
        //?} else {
        /^RenderSystem.disableCull();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(-1.0f, -1.0f);
        ^///?}

        //?if < 1.21.6 {
        /^RenderSystem.setShaderColor(1,1,1,1);
        ^///?}
    *///?}
}

    public void restoreRendererSystem() {


        //?if<26.2{
            /*//? if <1.21.11 {
            /^RenderSystem.lineWidth(1.0f);
            ^///?}

            //?if >=1.21.5{
            GlStateManager._enableDepthTest();
            GlStateManager._disablePolygonOffset();
            GlStateManager._enableCull();
            //?} else {
            /^RenderSystem.enableDepthTest();
            RenderSystem.disablePolygonOffset();
            RenderSystem.enableCull();
            ^///?}

            //?if < 1.21.6 {
            /^RenderSystem.setShaderColor(1,1,1,1);
            ^///?}
        *///?}
    }
}