package mypals.ml.shape;

import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shapeManagers.ShapeManager;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public abstract class Shape {
    public enum RenderingType {
        IMMEDIATE,   // Immediately rendered
        BATCH,      // Stored in bufferBuilder, batch uploads every frame
        BUFFERED    // Stored in VBO
    }
    public final RenderingType type;
    public final BiConsumer<MatrixStack, Shape> transform;
    public boolean seeThrough;
    public boolean isGroupedShape = false;

    protected Shape(RenderingType type, BiConsumer<MatrixStack, Shape> transform) {
        this.type = type;
        this.transform = transform;
        this.seeThrough = false;
    }
    protected Shape(RenderingType type, BiConsumer<MatrixStack, Shape> transform,boolean seeThrough) {
        this.type = type;
        this.transform = transform;
        this.seeThrough = seeThrough;
    }
    public void draw(ShapeBuilder builder, MatrixStack matrixStack) {
        matrixStack.push();
        this.transform.accept(matrixStack,this);
        builder.setPositionMatrix(matrixStack.peek().getPositionMatrix());
        draw(builder);
        matrixStack.pop();
    }
    public void draw(ShapeBuilder builder) {
        if(this.isGroupedShape) throw new UnsupportedOperationException("Grouped cant be rendered directly, use addGroup to extract it.");
    }
    public void addGroup(Identifier identifier) {
        if(this.isGroupedShape) throw new UnsupportedOperationException("This is not a grouped shape, use draw to render it.");
    }

}

