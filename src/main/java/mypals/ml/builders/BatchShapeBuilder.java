package mypals.ml.builders;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class BatchShapeBuilder extends ShapeBuilder {
    private boolean seeThrough;
    private boolean isBuilding = false;

    public BatchShapeBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix);
        this.seeThrough = seeThrough;
    }

    public void beginBatch(RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        isBuilding = true;
    }

    public void push(Consumer<ShapeBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        }
    }

    public void draw(Shape shape, Consumer<ShapeBuilder> builder, RenderMethod renderMethod) {
        beginBatch(renderMethod);
        push(builder);
        drawBatch();
    }

    public void drawBatch() {
        if (!isBuilding) {
            return;
        }

        if (seeThrough) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        BuiltBuffer builtBuffer = this.getBufferBuilder().end();
        //this.bufferBuilder = null;
        if (builtBuffer == null) {
            isBuilding = false;
            return;
        }
        BufferRenderer.drawWithGlobalProgram(builtBuffer);
        RenderSystem.enableDepthTest();
        isBuilding = false;
    }
}