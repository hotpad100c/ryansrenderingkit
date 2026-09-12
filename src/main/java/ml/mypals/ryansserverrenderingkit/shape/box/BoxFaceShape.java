package ml.mypals.ryansserverrenderingkit.shape.box;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.DrawableQuad;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.function.Consumer;

public class BoxFaceShape extends BoxShape implements DrawableQuad {

    public BoxFaceShape(Consumer<BoxTransformer> transform,
                        Vec3 min,
                        Vec3 max,
                        Color faceColor,
                        boolean seeThrough,
                        BoxConstructionType constructionType) {
        super(transform, min, max, faceColor, seeThrough, constructionType);
        generateRawGeometry(false);
    }

    @Deprecated
    public BoxFaceShape(Object ignored,
                        Consumer<BoxTransformer> transform,
                        Vec3 min,
                        Vec3 max,
                        Color faceColor,
                        boolean seeThrough,
                        BoxConstructionType constructionType) {
        this(transform, min, max, faceColor, seeThrough, constructionType);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();

        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 c = bt.getLocalPivot();
        Vec3 d = bt.getDimension(lerp);
        double hx = d.x * 0.5;
        double hy = d.y * 0.5;
        double hz = d.z * 0.5;

        modelVertexes.add(new Vec3(c.x - hx, c.y - hy, c.z - hz)); // 0
        modelVertexes.add(new Vec3(c.x + hx, c.y - hy, c.z - hz)); // 1
        modelVertexes.add(new Vec3(c.x + hx, c.y + hy, c.z - hz)); // 2
        modelVertexes.add(new Vec3(c.x - hx, c.y + hy, c.z - hz)); // 3
        modelVertexes.add(new Vec3(c.x - hx, c.y - hy, c.z + hz)); // 4
        modelVertexes.add(new Vec3(c.x + hx, c.y - hy, c.z + hz)); // 5
        modelVertexes.add(new Vec3(c.x + hx, c.y + hy, c.z + hz)); // 6
        modelVertexes.add(new Vec3(c.x - hx, c.y + hy, c.z + hz)); // 7

        indexBuffer = new int[]{
                0, 1, 2, 2, 3, 0,
                4, 6, 5, 6, 4, 7,
                0, 4, 1, 1, 4, 5,
                3, 2, 6, 6, 7, 3,
                0, 3, 4, 4, 3, 7,
                1, 5, 2, 2, 5, 6
        };
    }

    @Override
    public void initDisplays(ServerLevel level) {
        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 center = bt.getWorldPivot();
        Vec3 dims = bt.getDimension(false);
        Quaternionf rot = bt.getWorldRotation();

        Transformation transformation = DisplayTransformHelper.centeredBox(dims, rot);

        VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                .bright()
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(transformation);

        this.displays.add(display);
    }

    @Override
    public void updateDisplays() {
        if (this.displays.isEmpty()) return;

        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 center = bt.getWorldPivot();
        Vec3 dims = bt.getDimension(true);
        Quaternionf rot = bt.getWorldRotation();

        Transformation transformation = DisplayTransformHelper.centeredBox(dims, rot);

        VirtualDisplay display = this.displays.getFirst();
        display.pos(center.x, center.y, center.z)
                .blockState(getBlockState())
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(transformation);
    }
}
