package mypals.ml.builders.vertexBuilders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import mypals.ml.render.RenderMethod;
import mypals.ml.shape.Shape;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class BatchVertexBuilder extends VertexBuilder {
    private boolean isBuilding = false;
    public BatchVertexBuilder(Matrix4f modelViewMatrix, boolean seeThrough) {
        super(modelViewMatrix,seeThrough);
    }

    public void beginBatch(RenderMethod renderMethod) {
        begin(renderMethod);
        RenderSystem.setShader(renderMethod.shader());
        isBuilding = true;
    }

    public void push(Consumer<VertexBuilder> builder) {
        if (isBuilding) {
            builder.accept(this);
        }else{
            throw new IllegalStateException("BatchShapeBuilder is not building. Call beginBatch() before pushing shapes.");
        }
    }

    public void draw(Shape shape, Consumer<VertexBuilder> builder, RenderMethod renderMethod) {
        beginBatch(renderMethod);
        push(builder);
        drawBatch();
    }

    public void drawBatch() {
        if (!isBuilding) {
            return;
        }

        setUpRendererSystem(null);

        MeshData builtBuffer = this.getBufferBuilder().buildOrThrow();
        BufferUploader.drawWithShader(builtBuffer);

        restoreRendererSystem();

        isBuilding = false;
    }
}