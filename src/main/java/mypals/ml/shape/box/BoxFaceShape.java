package mypals.ml.shape.box;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableQuad;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.BiConsumer;


public class BoxFaceShape extends BoxShape implements DrawableQuad{
    public Color faceputColor;
    public BoxFaceShape(RenderingType type,
                        BiConsumer<BoxTransformer, Shape> transform,
                        Vec3 min,
                        Vec3 max,
                        Color faceputColor,
                        boolean seeThrough, BoxConstructionType constructionType)
    {
        super(type, transform,min,max, seeThrough,constructionType);
        this.faceputColor = faceputColor;
    }
    @Override
    public void draw(VertexBuilder builder) {
        this.renderFaces(builder);
    }

    private void renderFaces(VertexBuilder vertexBuilder) {
        vertexBuilder.putColor(faceputColor);

        Vec3 dimensions = this.getDimensions();
        Vec3 center = this.getCenter();

        Vec3 halfDimensions = dimensions.scale(0.5);
        double halfWidth = halfDimensions.x;
        double halfHeight = halfDimensions.y;
        double halfLength = halfDimensions.z;

        Vec3 v1 = new Vec3(center.x - halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3 v2 = new Vec3(center.x + halfWidth, center.y - halfHeight, center.z - halfLength);
        Vec3 v3 = new Vec3(center.x + halfWidth, center.y - halfHeight, center.z + halfLength);
        Vec3 v4 = new Vec3(center.x - halfWidth, center.y - halfHeight, center.z + halfLength);

        Vec3 v5 = new Vec3(center.x - halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3 v6 = new Vec3(center.x + halfWidth, center.y + halfHeight, center.z - halfLength);
        Vec3 v7 = new Vec3(center.x + halfWidth, center.y + halfHeight, center.z + halfLength);
        Vec3 v8 = new Vec3(center.x - halfWidth, center.y + halfHeight, center.z + halfLength);


        vertexBuilder.putVertex((float) v1.x, (float) v1.y, (float) v1.z); // v1
        vertexBuilder.putVertex((float) v5.x, (float) v5.y, (float) v5.z); // v5
        vertexBuilder.putVertex((float) v6.x, (float) v6.y, (float) v6.z); // v6
        vertexBuilder.putVertex((float) v2.x, (float) v2.y, (float) v2.z); // v2

        vertexBuilder.putVertex((float) v4.x, (float) v4.y, (float) v4.z); // v4
        vertexBuilder.putVertex((float) v3.x, (float) v3.y, (float) v3.z); // v3
        vertexBuilder.putVertex((float) v7.x, (float) v7.y, (float) v7.z); // v7
        vertexBuilder.putVertex((float) v8.x, (float) v8.y, (float) v8.z); // v8

        vertexBuilder.putVertex((float) v1.x, (float) v1.y, (float) v1.z); // v1
        vertexBuilder.putVertex((float) v4.x, (float) v4.y, (float) v4.z); // v4
        vertexBuilder.putVertex((float) v8.x, (float) v8.y, (float) v8.z); // v8
        vertexBuilder.putVertex((float) v5.x, (float) v5.y, (float) v5.z); // v5

        vertexBuilder.putVertex((float) v2.x, (float) v2.y, (float) v2.z); // v2
        vertexBuilder.putVertex((float) v6.x, (float) v6.y, (float) v6.z); // v6
        vertexBuilder.putVertex((float) v7.x, (float) v7.y, (float) v7.z); // v7
        vertexBuilder.putVertex((float) v3.x, (float) v3.y, (float) v3.z); // v3

        vertexBuilder.putVertex((float) v1.x, (float) v1.y, (float) v1.z); // v1
        vertexBuilder.putVertex((float) v2.x, (float) v2.y, (float) v2.z); // v2
        vertexBuilder.putVertex((float) v3.x, (float) v3.y, (float) v3.z); // v3
        vertexBuilder.putVertex((float) v4.x, (float) v4.y, (float) v4.z); // v4

        vertexBuilder.putVertex((float) v5.x, (float) v5.y, (float) v5.z); // v5
        vertexBuilder.putVertex((float) v8.x, (float) v8.y, (float) v8.z); // v8
        vertexBuilder.putVertex((float) v7.x, (float) v7.y, (float) v7.z); // v7
        vertexBuilder.putVertex((float) v6.x, (float) v6.y, (float) v6.z); // v6
    }
}
