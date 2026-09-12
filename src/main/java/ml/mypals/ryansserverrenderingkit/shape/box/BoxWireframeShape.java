package ml.mypals.ryansserverrenderingkit.shape.box;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.basics.core.LineLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.DrawableLine;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BoxWireframeShape extends BoxShape implements DrawableLine {

    public float edgeWidth;

    public BoxWireframeShape(Consumer<BoxTransformer> transform,
                             Vec3 vec1,
                             Vec3 vec2,
                             Color edgeColor,
                             boolean seeThrough,
                             float edgeWidth,
                             BoxConstructionType constructionType) {
        super(transform, vec1, vec2, edgeColor, seeThrough, constructionType);
        this.edgeWidth = Math.max(0.01f, edgeWidth);
        generateRawGeometry(false);
    }

    public BoxWireframeShape(Consumer<BoxTransformer> transform,
                             Vec3 vec1,
                             Vec3 vec2,
                             Color edgeColor,
                             float edgeWidth,
                             BoxConstructionType constructionType) {
        this(transform, vec1, vec2, edgeColor, false, edgeWidth, constructionType);
    }

    @Deprecated
    public BoxWireframeShape(Object ignored,
                             Consumer<BoxTransformer> transform,
                             Vec3 vec1,
                             Vec3 vec2,
                             Color edgeColor,
                             boolean seeThrough,
                             float edgeWidth,
                             BoxConstructionType constructionType) {
        this(transform, vec1, vec2, edgeColor, seeThrough, edgeWidth, constructionType);
    }

    @Deprecated
    public BoxWireframeShape(Object ignored,
                             Consumer<BoxTransformer> transform,
                             Vec3 vec1,
                             Vec3 vec2,
                             Color edgeColor,
                             float edgeWidth,
                             BoxConstructionType constructionType) {
        this(transform, vec1, vec2, edgeColor, false, edgeWidth, constructionType);
    }

    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((LineLikeShape.SimpleLineTransformer) this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void setLineWidth(float width) {
        this.edgeWidth = width;
        if (this.transformer instanceof LineLikeShape.SimpleLineTransformer slt) {
            slt.setWidth(width);
        }
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

        Vec3 v0 = new Vec3(c.x - hx, c.y - hy, c.z - hz);
        Vec3 v1 = new Vec3(c.x + hx, c.y - hy, c.z - hz);
        Vec3 v2 = new Vec3(c.x + hx, c.y - hy, c.z + hz);
        Vec3 v3 = new Vec3(c.x - hx, c.y - hy, c.z + hz);
        Vec3 v4 = new Vec3(c.x - hx, c.y + hy, c.z - hz);
        Vec3 v5 = new Vec3(c.x + hx, c.y + hy, c.z - hz);
        Vec3 v6 = new Vec3(c.x + hx, c.y + hy, c.z + hz);
        Vec3 v7 = new Vec3(c.x - hx, c.y + hy, c.z + hz);

        modelVertexes.add(v0); // 0
        modelVertexes.add(v1); // 1
        modelVertexes.add(v2); // 2
        modelVertexes.add(v3); // 3
        modelVertexes.add(v4); // 4
        modelVertexes.add(v5); // 5
        modelVertexes.add(v6); // 6
        modelVertexes.add(v7); // 7

        indexBuffer = new int[]{
                // Bottom
                0, 1, 1, 2, 2, 3, 3, 0,
                // Top
                4, 5, 5, 6, 6, 7, 7, 4,
                // Vert
                0, 4, 1, 5, 2, 6, 3, 7
        };
    }

    private List<Vec3> getWorldCorners(boolean lerp) {
        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 center = bt.getWorldPivot();
        Vec3 dims = bt.getDimension(lerp);
        Quaternionf rot = bt.getWorldRotation();

        float hx = (float) (dims.x * 0.5);
        float hy = (float) (dims.y * 0.5);
        float hz = (float) (dims.z * 0.5);

        Vector3f[] local = new Vector3f[]{
                new Vector3f(-hx, -hy, -hz),
                new Vector3f(+hx, -hy, -hz),
                new Vector3f(+hx, -hy, +hz),
                new Vector3f(-hx, -hy, +hz),
                new Vector3f(-hx, +hy, -hz),
                new Vector3f(+hx, +hy, -hz),
                new Vector3f(+hx, +hy, +hz),
                new Vector3f(-hx, +hy, +hz),
        };

        List<Vec3> result = new ArrayList<>(8);
        for (Vector3f v : local) {
            rot.transform(v);
            result.add(new Vec3(center.x + v.x, center.y + v.y, center.z + v.z));
        }
        return result;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 center = bt.getWorldPivot();
        Vec3 dims = bt.getDimension(false);
        Quaternionf rot = bt.getWorldRotation();

        float hx = (float) (dims.x * 0.5);
        float hy = (float) (dims.y * 0.5);
        float hz = (float) (dims.z * 0.5);

        Vector3f[] local = new Vector3f[]{
                new Vector3f(-hx, -hy, -hz),
                new Vector3f(+hx, -hy, -hz),
                new Vector3f(+hx, -hy, +hz),
                new Vector3f(-hx, -hy, +hz),
                new Vector3f(-hx, +hy, -hz),
                new Vector3f(+hx, +hy, -hz),
                new Vector3f(+hx, +hy, +hz),
                new Vector3f(-hx, +hy, +hz),
        };

        int[] edges = new int[]{
                0, 1, 1, 2, 2, 3, 3, 0,
                4, 5, 5, 6, 6, 7, 7, 4,
                0, 4, 1, 5, 2, 6, 3, 7
        };

        for (int i = 0; i < 12; i++) {
            Vector3f localA = local[edges[i * 2]];
            Vector3f localB = local[edges[i * 2 + 1]];
            Transformation t = DisplayTransformHelper.localSegment(localA, localB, edgeWidth, rot, null);

            VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        if (this.displays.size() != 12) {
            ServerLevel lvl = this.displays.isEmpty() ? this.level : this.displays.getFirst().getLevel();
            removeDisplays();
            if (lvl != null) {
                initDisplays(lvl);
            }
            return;
        }

        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 center = bt.getWorldPivot();
        Vec3 dims = bt.getDimension(true);
        Quaternionf rot = bt.getWorldRotation();

        VirtualDisplay first = this.displays.getFirst();
        Vec3 spawnPos = new Vec3(first.getEntity().getX(), first.getEntity().getY(), first.getEntity().getZ());
        Vec3 centerOffset = center.subtract(spawnPos);

        float hx = (float) (dims.x * 0.5);
        float hy = (float) (dims.y * 0.5);
        float hz = (float) (dims.z * 0.5);

        Vector3f[] local = new Vector3f[]{
                new Vector3f(-hx, -hy, -hz),
                new Vector3f(+hx, -hy, -hz),
                new Vector3f(+hx, -hy, +hz),
                new Vector3f(-hx, -hy, +hz),
                new Vector3f(-hx, +hy, -hz),
                new Vector3f(+hx, +hy, -hz),
                new Vector3f(+hx, +hy, +hz),
                new Vector3f(-hx, +hy, +hz),
        };

        int[] edges = new int[]{
                0, 1, 1, 2, 2, 3, 3, 0,
                4, 5, 5, 6, 6, 7, 7, 4,
                0, 4, 1, 5, 2, 6, 3, 7
        };

        for (int i = 0; i < 12; i++) {
            Vector3f localA = local[edges[i * 2]];
            Vector3f localB = local[edges[i * 2 + 1]];
            Transformation t = DisplayTransformHelper.localSegment(localA, localB, edgeWidth, rot, centerOffset);
            VirtualDisplay display = this.displays.get(i);
            display.blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }
    }
}
