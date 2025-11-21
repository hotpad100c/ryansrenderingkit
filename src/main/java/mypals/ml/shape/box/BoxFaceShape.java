package mypals.ml.shape.box;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableQuad;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class BoxFaceShape extends BoxShape implements DrawableQuad{

    public BoxFaceShape(RenderingType type,
                        Consumer<BoxTransformer> transform,
                        Vec3 min,
                        Vec3 max,
                        Color faceputColor,
                        boolean seeThrough, BoxConstructionType constructionType)
    {
        super(type, transform,min,max,faceputColor, seeThrough,constructionType);
        generateRawGeometry(false);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();

        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 c = bt.getLocalPivot();
        Vec3 d = bt.getDimension(lerp);
        double hx = d.x * 0.5;
        double hy = d.y * 0.5;
        double hz = d.z * 0.5;

        model_vertexes.add(new Vec3(c.x - hx, c.y - hy, c.z - hz)); // 0
        model_vertexes.add(new Vec3(c.x + hx, c.y - hy, c.z - hz)); // 1
        model_vertexes.add(new Vec3(c.x + hx, c.y + hy, c.z - hz)); // 2
        model_vertexes.add(new Vec3(c.x - hx, c.y + hy, c.z - hz)); // 3
        model_vertexes.add(new Vec3(c.x - hx, c.y - hy, c.z + hz)); // 4
        model_vertexes.add(new Vec3(c.x + hx, c.y - hy, c.z + hz)); // 5
        model_vertexes.add(new Vec3(c.x + hx, c.y + hy, c.z + hz)); // 6
        model_vertexes.add(new Vec3(c.x - hx, c.y + hy, c.z + hz)); // 7

        indexBuffer = new int[] {
                // -Z
                0, 1, 2, 2, 3, 0,
                // +Z
                4, 6, 5, 6, 4, 7,
                // -Y
                0, 4, 1, 1, 4, 5,
                // +Y
                3, 2, 6, 6, 7, 3,
                // -X
                0, 3, 4, 4, 3, 7,
                // +X
                1, 5, 2, 2, 5, 6
        };
    }
}
