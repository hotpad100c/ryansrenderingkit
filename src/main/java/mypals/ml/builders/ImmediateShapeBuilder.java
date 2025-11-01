package mypals.ml.builders;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class ImmediateShapeBuilder extends ShapeBuilder {

    public ImmediateShapeBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix, seeThrough);
    }

    public void draw(Shape shape, Consumer<ShapeBuilder> builder, RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        builder.accept(this);

        setUpRendererSystem(shape);

        BuiltBuffer builtBuffer = this.getBufferBuilder().end();
        BufferRenderer.drawWithGlobalProgram(builtBuffer);

        restoreRendererSystem();
    }
}