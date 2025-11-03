package mypals.ml.shape.box;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.ExtractableShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.BiConsumer;

public class WireframedBoxShape extends BoxShape implements ExtractableShape {
    public Color faceputColor;
    public Color edgeputColor;
    public float edgeWidth;
    public boolean lineSeeThrough;
    public BoxConstructionType constructionType;

    public BiConsumer<BoxTransformer, Shape> transformFunction;
    public WireframedBoxShape(RenderingType type,
                              BiConsumer<BoxTransformer, Shape> transform,
                              Vec3 min,
                              Vec3 max,
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
    @Override
    public void addGroup(ResourceLocation identifier) {
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
