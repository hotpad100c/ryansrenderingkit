package mypals.ml.builders;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class BatchShapeBuilder extends ShapeBuilder {
    private boolean isBuilding = false;
    public BatchShapeBuilder(Matrix4f modelViewMatrix, boolean seeThrough,boolean cullFace) {
        super(modelViewMatrix,seeThrough,cullFace);
    }

    public void beginBatch(RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        isBuilding = true;
    }

    public void push(Consumer<ShapeBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        }else{
            throw new IllegalStateException("BatchShapeBuilder is not building. Call beginBatch() before pushing shapes.");
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

        setUpRendererSystem(null);

        BuiltBuffer builtBuffer = this.getBufferBuilder().end();
        BufferRenderer.drawWithGlobalProgram(builtBuffer);

        restoreRendererSystem();

        isBuilding = false;
    }
}