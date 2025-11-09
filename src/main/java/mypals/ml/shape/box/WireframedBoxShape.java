package mypals.ml.shape.box;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.ExtractableShape;
import mypals.ml.shapeManagers.ShapeManagers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WireframedBoxShape extends BoxShape implements ExtractableShape {
    public Color faceputColor;
    public Color edgeputColor;
    public float edgeWidth;
    public boolean lineSeeThrough;
    public BoxConstructionType constructionType;

    public Consumer<BoxTransformer> recordedTransformFunction;
    public WireframedBoxShape(RenderingType type,
                              Consumer<BoxTransformer> transform,
                              Vec3 min,
                              Vec3 max,
                              Color faceputColor,
                              Color edgeputColor,
                              float edgeWidth,
                              boolean seeThrough,
                              boolean lineSeeThrough,BoxConstructionType constructionType)
    {
        super(type, transform,min,max,faceputColor, seeThrough,constructionType);

        this.recordedTransformFunction = transform;
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
                        recordedTransformFunction,
                        this.getMin(),
                        this.getMax(),
                        this.edgeputColor,
                        this.lineSeeThrough,
                        this.edgeWidth,
                        constructionType
                )
        );
        ShapeManagers.TRIANGLES_SHAPE_MANAGER.addShape(
                identifier.withPath(identifier.getPath()+"/face"),
                new BoxFaceShape(
                    this.type,
                    recordedTransformFunction,
                    this.getMin(),
                    this.getMax(),
                    this.faceputColor,
                    this.seeThrough,
                    constructionType
                )
        );
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {

    }
}
