package mypals.ml.builderManager;

import mypals.ml.builders.vertexBuilders.EmptyVertexBuilder;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class EmptyBuilderManager {
    public String id;
    public EmptyVertexBuilder emptyVertexBuilder;

    public EmptyBuilderManager(Matrix4f matrix4f, String id) {
        this.id = id;
        emptyVertexBuilder = new EmptyVertexBuilder(matrix4f);
    }

    public void draw(Consumer<VertexBuilder> builder) {
        emptyVertexBuilder.draw(builder);
    }

    public void updateMatrix(Matrix4f modelViewMatrix) {
        emptyVertexBuilder.setPositionMatrix(modelViewMatrix);
    }
}

