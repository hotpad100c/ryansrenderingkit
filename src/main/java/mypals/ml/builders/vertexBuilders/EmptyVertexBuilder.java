package mypals.ml.builders.vertexBuilders;
import org.joml.Matrix4f;
import java.util.function.Consumer;

public class EmptyVertexBuilder extends VertexBuilder {

    public EmptyVertexBuilder(Matrix4f modelViewMatrix) {
        super(modelViewMatrix, false);
    }
    public void draw(Consumer<VertexBuilder> vertexBuilderConsumer) {
        vertexBuilderConsumer.accept(this);
    }
}