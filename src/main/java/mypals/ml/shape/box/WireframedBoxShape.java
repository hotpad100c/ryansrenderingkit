package mypals.ml.shape.box;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.ExtractableShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.function.BiConsumer;

public class WireframedBoxShape extends BoxShape implements ExtractableShape {
    public Color faceputColor;
    public Color edgeputColor;
    public float edgeWidth;
    public boolean lineSeeThrough = false;
    public BoxConstructionType constructionType;

    public BiConsumer<BoxTransformer, Shape> transformFunction;
    public WireframedBoxShape(RenderingType type,
                              BiConsumer<BoxTransformer, Shape> transform,
                              Vec3d min,
                              Vec3d max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough,BoxConstructionType constructionType)
    {
        super(type, transform,min,max, seeThrough,constructionType);
        this.transformer = new BoxTransformer(this);
        this.transformFunction = transform;
        this.faceputColor = faceputColor;
        this.edgeputColor = edgeputColor;
        this.edgeWidth = edgeWidth;
        this.lineSeeThrough = lineSeeThrough;
        this.constructionType = constructionType;
    }
    public WireframedBoxShape(RenderingType type,
                              Vec3d min,
                              Vec3d max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough,BoxConstructionType constructionType)
    {
        this(type, (transformer, shape) -> {},min,max,faceputColor,edgeputColor,edgeWidth, seeThrough,lineSeeThrough,constructionType);
    }
    @Override
    public void addGroup(Identifier identifier) {
        ShapeManagers.LINES_SHAPE_MANAGER.addShape(
                identifier.withPath(identifier.getPath()+"/wireframe"),
                new BoxWireframeShape(
                        this.type,
                        this.transformFunction,
                        this.getMin(),
                        this.getMax(),
                        this.edgeputColor,
                        this.lineSeeThrough,
                        this.edgeWidth,
                        constructionType
                )
        );
        ShapeManagers.QUADS_SHAPE_MANAGER.addShape(
                identifier.withPath(identifier.getPath()+"/face"),
                new BoxFaceShape(
                    this.type,
                    this.transformFunction,
                    this.getMin(),
                    this.getMax(),
                    this.faceputColor,
                    this.seeThrough,
                    constructionType
                )
        );
    }
}
