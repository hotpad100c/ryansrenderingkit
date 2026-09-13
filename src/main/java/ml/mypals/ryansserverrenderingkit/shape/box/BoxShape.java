package ml.mypals.ryansserverrenderingkit.shape.box;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.BoxLikeShape;
import ml.mypals.ryansserverrenderingkit.utils.Helpers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BoxShape extends Shape implements BoxLikeShape {

    public enum BoxConstructionType {CENTER_AND_DIMENSIONS, CORNERS}

    public BoxShape(Consumer<BoxTransformer> transform,
                    Vec3 vec1,
                    Vec3 vec2,
                    Color color,
                    boolean seeThrough,
                    BoxConstructionType constructionType) {
        super(color, seeThrough);

        this.transformFunction = (t) -> {
            if (transform != null) {
                transform.accept((BoxTransformer) this.transformer);
            }
        };

        if (constructionType == BoxConstructionType.CENTER_AND_DIMENSIONS) {
            this.transformer = new BoxTransformer(this, new Vec3(Math.abs(vec2.x), Math.abs(vec2.y), Math.abs(vec2.z)), vec1);
        } else {
            Vec3 min = Helpers.min(vec1, vec2);
            Vec3 max = Helpers.max(vec1, vec2);
            Vec3 dims = max.subtract(min);
            Vec3 center = min.add(dims.scale(0.5));
            this.transformer = new BoxTransformer(this, dims, center);
        }

        syncLastToTarget();
        generateRawGeometry(false);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        Vec3 dimensions = ((BoxTransformer) transformer).getDimension(lerp);
        double hx = dimensions.x * 0.5D;
        double hy = dimensions.y * 0.5D;
        double hz = dimensions.z * 0.5D;
        modelVertexes.add(new Vec3(-hx, -hy, -hz));
        modelVertexes.add(new Vec3(hx, -hy, -hz));
        modelVertexes.add(new Vec3(hx, -hy, hz));
        modelVertexes.add(new Vec3(-hx, -hy, hz));
        modelVertexes.add(new Vec3(-hx, hy, -hz));
        modelVertexes.add(new Vec3(hx, hy, -hz));
        modelVertexes.add(new Vec3(hx, hy, hz));
        modelVertexes.add(new Vec3(-hx, hy, hz));
        indexBuffer = new int[]{
                0, 1, 2, 2, 3, 0, 4, 6, 5, 6, 4, 7,
                0, 4, 1, 1, 4, 5, 3, 2, 6, 6, 7, 3,
                0, 3, 4, 4, 3, 7, 1, 5, 2, 2, 5, 6
        };
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 center = transformer.getWorldPivot();
        if (renderFace) {
            for (Transformation face : getLocalFaces(false, null)) {
                displays.add(VirtualDisplay.solidTextPanel(level, center.x, center.y, center.z, baseColor.getRGB())
                        .bright().seeThrough(seeThrough, baseColor.getRGB()).transform(face));
            }
        }
        if (renderWireframe) {
            Quaternionf rotation = transformer.getWorldRotation();
            for (Vector3f[] edge : getLocalEdges(false)) {
                displays.add(VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                        .bright().seeThrough(seeThrough, baseColor.getRGB())
                        .transform(DisplayTransformHelper.localSegment(
                                edge[0], edge[1], wireframeWidth, rotation, null)));
            }
        }
    }

    @Override
    public void updateDisplays() {
        Vec3 center = transformer.getWorldPivot();
        Vec3 centerOffset = displays.isEmpty() ? null : center.subtract(new Vec3(
                displays.getFirst().getEntity().getX(), displays.getFirst().getEntity().getY(),
                displays.getFirst().getEntity().getZ()));
        List<Transformation> faces = renderFace ? getLocalFaces(true, centerOffset) : List.of();
        List<Vector3f[]> edges = renderWireframe ? getLocalEdges(true) : List.of();
        if (displays.size() != faces.size() + edges.size()) {
            ServerLevel targetLevel = displays.isEmpty() ? level : displays.getFirst().getLevel();
            removeDisplays();
            if (targetLevel != null) initDisplays(targetLevel);
            return;
        }

        int displayIndex = 0;
        for (Transformation face : faces) {
            displays.get(displayIndex++).backgroundColor(baseColor.getRGB())
                    .seeThrough(seeThrough, baseColor.getRGB()).transform(face);
        }
        Quaternionf rotation = transformer.getWorldRotation();
        for (Vector3f[] edge : edges) {
            displays.get(displayIndex++).blockState(getBlockState())
                    .seeThrough(seeThrough, baseColor.getRGB())
                    .transform(DisplayTransformHelper.localSegment(
                            edge[0], edge[1], wireframeWidth, rotation, centerOffset));
        }
    }

    private List<Transformation> getLocalFaces(boolean lerp, Vec3 centerOffset) {
        Vec3 d = ((BoxTransformer) transformer).getDimension(lerp);
        float hx = (float) d.x * 0.5F;
        float hy = (float) d.y * 0.5F;
        float hz = (float) d.z * 0.5F;
        Quaternionf rotation = transformer.getWorldRotation();
        List<Transformation> faces = new ArrayList<>(6);
        // -Z face
        addFace(faces, new Vector3f(-hx, -hy, -hz),  new Vector3f(0, 2 * hy, 0), new Vector3f(2 * hx, 0, 0), rotation, centerOffset);
        // +Z face
        addFace(faces, new Vector3f(hx, -hy, hz), new Vector3f(0, 2 * hy, 0), new Vector3f(-2 * hx, 0, 0),   rotation, centerOffset);
        // -Y face (bottom)
        addFace(faces, new Vector3f(-hx, -hy, hz), new Vector3f(0, 0, -2 * hz), new Vector3f(2 * hx, 0, 0),  rotation, centerOffset);
        // +Y face (top)
        addFace(faces, new Vector3f(-hx, hy, -hz), new Vector3f(0, 0, 2 * hz),  new Vector3f(2 * hx, 0, 0), rotation, centerOffset);
        // -X face
        addFace(faces, new Vector3f(-hx, -hy, -hz),  new Vector3f(0, 0, 2 * hz), new Vector3f(0, 2 * hy, 0), rotation, centerOffset);
        // +X face
        addFace(faces, new Vector3f(hx, -hy, hz),  new Vector3f(0, 0, -2 * hz), new Vector3f(0, 2 * hy, 0), rotation, centerOffset);
        return faces;
    }

    private void addFace(List<Transformation> faces, Vector3f origin, Vector3f x, Vector3f y,
                         Quaternionf rotation, Vec3 centerOffset) {
        Transformation face = DisplayTransformHelper.localTextPanel(origin, x, y, rotation, centerOffset);
        if (face != null) faces.add(face);
    }

    private List<Vector3f[]> getLocalEdges(boolean lerp) {
        generateRawGeometry(lerp);
        int[] edgeIndices = {0, 1, 1, 2, 2, 3, 3, 0, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7};
        List<Vector3f[]> edges = new ArrayList<>(12);
        for (int i = 0; i < edgeIndices.length; i += 2) {
            Vec3 a = modelVertexes.get(edgeIndices[i]);
            Vec3 b = modelVertexes.get(edgeIndices[i + 1]);
            edges.add(new Vector3f[]{new Vector3f((float) a.x, (float) a.y, (float) a.z),
                    new Vector3f((float) b.x, (float) b.y, (float) b.z)});
        }
        return edges;
    }

    @Override
    public Vec3 getMin() {
        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 half = bt.getDimension(false).scale(0.5);
        return bt.getWorldPivot().subtract(half);
    }

    @Override
    public Vec3 getMax() {
        BoxTransformer bt = (BoxTransformer) transformer;
        Vec3 half = bt.getDimension(false).scale(0.5);
        return bt.getWorldPivot().add(half);
    }

    @Override
    public void setMin(Vec3 min) {
        Vec3 max = getMax();
        Vec3 center = min.add(max).scale(0.5);
        Vec3 dims = max.subtract(min);
        BoxTransformer bt = (BoxTransformer) transformer;
        bt.setShapeWorldPivot(center);
        bt.setDimension(dims);
    }

    @Override
    public void setMax(Vec3 max) {
        Vec3 min = getMin();
        Vec3 center = min.add(max).scale(0.5);
        Vec3 dims = max.subtract(min);
        BoxTransformer bt = (BoxTransformer) transformer;
        bt.setShapeWorldPivot(center);
        bt.setDimension(dims);
    }

    public void setDimension(Vec3 dimensions) {
        ((BoxTransformer) transformer).setDimension(
                new Vec3(Math.abs(dimensions.x), Math.abs(dimensions.y), Math.abs(dimensions.z))
        );
    }

    public void setCorners(Vec3 corner1, Vec3 corner2) {
        Vec3 min = Helpers.min(corner1, corner2);
        Vec3 max = Helpers.max(corner1, corner2);
        Vec3 center = min.add(max).scale(0.5);
        Vec3 dims = max.subtract(min);
        BoxTransformer bt = (BoxTransformer) transformer;
        bt.setShapeWorldPivot(center);
        bt.setDimension(dims);
    }

    public void forceSetMax(Vec3 max) {
        setMax(max);
        ((BoxTransformer) this.transformer).boxModelInfo.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetMin(Vec3 min) {
        setMin(min);
        ((BoxTransformer) this.transformer).boxModelInfo.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetDimensions(Vec3 dim) {
        setDimension(dim);
        ((BoxTransformer) this.transformer).boxModelInfo.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetCorners(Vec3 c1, Vec3 c2) {
        setCorners(c1, c2);
        ((BoxTransformer) this.transformer).boxModelInfo.syncLastToTarget();
        generateRawGeometry(false);
    }

    @Override
    public void normalizeBounds() {
        Vec3 min = Helpers.min(getMin(), getMax());
        Vec3 max = Helpers.max(getMin(), getMax());
        setCorners(min, max);
    }
}
