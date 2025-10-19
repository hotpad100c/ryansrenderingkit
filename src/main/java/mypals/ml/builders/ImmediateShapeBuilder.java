package mypals.ml.builders;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class ImmediateShapeBuilder extends ShapeBuilder {
    private boolean seeThrough;

    public ImmediateShapeBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix);
        this.seeThrough = seeThrough;
    }

    public void draw(Shape shape, Consumer<ShapeBuilder> builder, RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        builder.accept(this);
        if (seeThrough) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        BuiltBuffer builtBuffer = this.getBufferBuilder().end();
        //this.bufferBuilder = null;
        if (builtBuffer == null) {
            return;
        }
        BufferRenderer.drawWithGlobalProgram(builtBuffer);
        RenderSystem.enableDepthTest();
    }
}