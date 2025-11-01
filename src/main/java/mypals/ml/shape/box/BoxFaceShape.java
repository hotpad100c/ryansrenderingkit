package mypals.ml.shape.box;

import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableQuad;

import net.minecraft.util.math.Vec3d;


import java.awt.*;
import java.util.function.BiConsumer;


public class BoxFaceShape extends BoxShape implements DrawableQuad{
    public Color faceputColor;
    public BoxFaceShape(RenderingType type,
                        BiConsumer<BoxTransformer, Shape> transform,
                        Vec3d min,
                        Vec3d max,
                        Color faceputColor,
                        boolean seeThrough, BoxConstructionType constructionType)
    {
        super(type, transform,min,max, seeThrough,constructionType);
        this.faceputColor = faceputColor;
    }
    public BoxFaceShape(RenderingType type,
                        Vec3d min,
                        Vec3d max,
                        Color faceputColor,
                        boolean seeThrough,BoxConstructionType constructionType)
    {
        this(type, (transformer, shape) -> {}, min, max, faceputColor, seeThrough,constructionType);
    }
    @Override
    public void draw(ShapeBuilder builder) {
        this.renderFaces(builder);
    }

    private void renderFaces(ShapeBuilder shapeBuilder) {
        shapeBuilder.putColor(faceputColor);

        Vec3d dimensions = this.getDimensions();
        Vec3d center = this.getCenter();

        Vec3d halfDimensions = dimensions.multiply(0.5);
        double halfWidth = halfDimensions.x;
        double halfHeight = halfDimensions.y;
        double halfLength = halfDimensions.z;

        Vec3d v1 = new Vec3d(center.x - halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3d v2 = new Vec3d(center.x + halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3d v3 = new Vec3d(center.x + halfWidth, center.y - halfHeight, center.z + halfLength);
        Vec3d v4 = new Vec3d(center.x - halfWidth, center.y - halfHeight, center.z + halfLength);

        Vec3d v5 = new Vec3d(center.x - halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3d v6 = new Vec3d(center.x + halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3d v7 = new Vec3d(center.x + halfWidth, center.y + halfHeight, center.z + halfLength);
        Vec3d v8 = new Vec3d(center.x - halfWidth, center.y + halfHeight, center.z + halfLength);


        shapeBuilder.putVertex((float) v1.x, (float) v1.y, (float) v1.z); // v1
        shapeBuilder.putVertex((float) v5.x, (float) v5.y, (float) v5.z); // v5
        shapeBuilder.putVertex((float) v6.x, (float) v6.y, (float) v6.z); // v6
        shapeBuilder.putVertex((float) v2.x, (float) v2.y, (float) v2.z); // v2

        shapeBuilder.putVertex((float) v4.x, (float) v4.y, (float) v4.z); // v4
        shapeBuilder.putVertex((float) v3.x, (float) v3.y, (float) v3.z); // v3
        shapeBuilder.putVertex((float) v7.x, (float) v7.y, (float) v7.z); // v7
        shapeBuilder.putVertex((float) v8.x, (float) v8.y, (float) v8.z); // v8

        shapeBuilder.putVertex((float) v1.x, (float) v1.y, (float) v1.z); // v1
        shapeBuilder.putVertex((float) v4.x, (float) v4.y, (float) v4.z); // v4
        shapeBuilder.putVertex((float) v8.x, (float) v8.y, (float) v8.z); // v8
        shapeBuilder.putVertex((float) v5.x, (float) v5.y, (float) v5.z); // v5

        shapeBuilder.putVertex((float) v2.x, (float) v2.y, (float) v2.z); // v2
        shapeBuilder.putVertex((float) v6.x, (float) v6.y, (float) v6.z); // v6
        shapeBuilder.putVertex((float) v7.x, (float) v7.y, (float) v7.z); // v7
        shapeBuilder.putVertex((float) v3.x, (float) v3.y, (float) v3.z); // v3

        shapeBuilder.putVertex((float) v1.x, (float) v1.y, (float) v1.z); // v1
        shapeBuilder.putVertex((float) v2.x, (float) v2.y, (float) v2.z); // v2
        shapeBuilder.putVertex((float) v3.x, (float) v3.y, (float) v3.z); // v3
        shapeBuilder.putVertex((float) v4.x, (float) v4.y, (float) v4.z); // v4

        shapeBuilder.putVertex((float) v5.x, (float) v5.y, (float) v5.z); // v5
        shapeBuilder.putVertex((float) v8.x, (float) v8.y, (float) v8.z); // v8
        shapeBuilder.putVertex((float) v7.x, (float) v7.y, (float) v7.z); // v7
        shapeBuilder.putVertex((float) v6.x, (float) v6.y, (float) v6.z); // v6
    }
}
