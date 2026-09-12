package ml.mypals.ryansserverrenderingkit.shape.box;

import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.ExtractableShape;
import ml.mypals.ryansserverrenderingkit.shapeManagers.ShapeManagers;
import net.minecraft.resources.Identifier;
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

    public WireframedBoxShape(Consumer<BoxTransformer> transform,
                              Vec3 min,
                              Vec3 max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough, BoxConstructionType constructionType) {
        super(transform, min, max, faceputColor, seeThrough, constructionType);

        this.recordedTransformFunction = transform;
        this.faceputColor = faceputColor;
        this.edgeputColor = edgeputColor;
        this.edgeWidth = edgeWidth;
        this.lineSeeThrough = lineSeeThrough;
        this.constructionType = constructionType;
    }

    @Deprecated
    public WireframedBoxShape(Object ignored,
                              Consumer<BoxTransformer> transform,
                              Vec3 min,
                              Vec3 max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough, BoxConstructionType constructionType) {
        this(transform, min, max, faceputColor, edgeputColor, edgeWidth, seeThrough, lineSeeThrough, constructionType);
    }

    @Override
    public void addGroup(Identifier identifier) {
        boxWireframeShape = new BoxWireframeShape(
                recordedTransformFunction,
                this.getMin(),
                this.getMax(),
                this.edgeputColor,
                this.lineSeeThrough,
                this.edgeWidth,
                constructionType
        );
        boxWireframeShape.level(this.level).allDim(this.allDim).visibleTo(this.viewerFilter);
        if (this.customBlockState != null) boxWireframeShape.block(this.customBlockState);

        ShapeManagers.addShape(
                identifier.withPath(identifier.getPath() + "/wireframe"),
                boxWireframeShape
        );

        boxFaceShape = new BoxFaceShape(
                recordedTransformFunction,
                this.getMin(),
                this.getMax(),
                this.faceputColor,
                this.seeThrough,
                constructionType
        );
        boxFaceShape.level(this.level).allDim(this.allDim).visibleTo(this.viewerFilter);
        if (this.customBlockState != null) boxFaceShape.block(this.customBlockState);

        ShapeManagers.addShape(
                identifier.withPath(identifier.getPath() + "/face"),
                boxFaceShape
        );
    }

    @Override
    public void discard() {
        children.forEach(Shape::discard);
        ShapeManagers.removeShapes(this.id);
    }
}
