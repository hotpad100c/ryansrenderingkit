package ml.mypals.ryansrenderingkit.shape.box;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.tags.ExtractableShape;
import ml.mypals.ryansrenderingkit.shapeManagers.ShapeManagers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.function.Consumer;

public class WireframedBoxShape extends BoxShape implements ExtractableShape {
    public Color faceputColor;
    public Color edgeputColor;
    public float edgeWidth;
    public boolean lineSeeThrough;
    public BoxConstructionType constructionType;
    private BoxWireframeShape boxWireframeShape;
    private BoxFaceShape boxFaceShape;
    public Consumer<BoxTransformer> recordedTransformFunction;

    public WireframedBoxShape(RenderingType type,
                              Consumer<BoxTransformer> transform,
                              Vec3 min,
                              Vec3 max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough, BoxConstructionType constructionType) {
        super(type, transform, min, max, faceputColor, seeThrough, constructionType);

        this.recordedTransformFunction = transform;
        this.faceputColor = faceputColor;
        this.edgeputColor = edgeputColor;
        this.edgeWidth = edgeWidth;
        this.lineSeeThrough = lineSeeThrough;
        this.constructionType = constructionType;
    }

    @Override
    public void addGroup(ResourceLocation identifier) {
        boxWireframeShape = new BoxWireframeShape(
                this.type,
                recordedTransformFunction,
                this.getMin(),
                this.getMax(),
                this.edgeputColor,
                this.lineSeeThrough,
                this.edgeWidth,
                constructionType
        );
        ShapeManagers.LINES_SHAPE_MANAGER.addShape(
                identifier.withPath(identifier.getPath() + "/wireframe"),
                boxWireframeShape
        );
        boxFaceShape = new BoxFaceShape(
                this.type,
                recordedTransformFunction,
                this.getMin(),
                this.getMax(),
                this.faceputColor,
                this.seeThrough,
                constructionType
        );
        ShapeManagers.TRIANGLES_SHAPE_MANAGER.addShape(
                identifier.withPath(identifier.getPath() + "/face"),
                boxFaceShape
        );
    }
    @Override
    public void discard() {
        children.forEach(Shape::discard);
        ShapeManagers.removeShapes(this.id);
    }
    @Override
    protected void generateRawGeometry(boolean lerp) {

    }
}
