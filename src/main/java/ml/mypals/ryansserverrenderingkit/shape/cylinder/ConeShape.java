package ml.mypals.ryansserverrenderingkit.shape.cylinder;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConeShape extends CylinderShape {

    public ConeShape(Consumer<CylinderTransformer> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius, float height, Color color, boolean seeThrough) {
        super(transform, circleAxis, center, segments, radius, height, color, seeThrough);
    }

    @Deprecated
    public ConeShape(Object ignored, Consumer<CylinderTransformer> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius, float height, Color color, boolean seeThrough) {
        this(transform, circleAxis, center, segments, radius, height, color, seeThrough);
    }

    private List<Vec3> generateConeVertices(double halfH, int segments, float radius, CircleAxis axis) {
        List<Vec3> vertices = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            Vec3 point = switch (axis) {
                case X -> new Vec3(-halfH, radius * c, radius * s);
                case Y -> new Vec3(radius * c, -halfH, radius * s);
                case Z -> new Vec3(radius * c, radius * s, -halfH);
            };
            vertices.add(point);
        }

        Vec3 apex = switch (axis) {
            case X -> new Vec3(halfH, 0, 0);
            case Y -> new Vec3(0, halfH, 0);
            case Z -> new Vec3(0, 0, halfH);
        };
        vertices.add(apex);

        Vec3 baseCenter = switch (axis) {
            case X -> new Vec3(-halfH, 0, 0);
            case Y -> new Vec3(0, -halfH, 0);
            case Z -> new Vec3(0, 0, -halfH);
        };
        vertices.add(baseCenter);

        return vertices;
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        List<Integer> indices = new ArrayList<>();

        float height = ((CylinderTransformer) this.transformer).getHeight(lerp);
        int segments = ((CylinderTransformer) this.transformer).getSegments(lerp);
        float radius = ((CylinderTransformer) this.transformer).getRadius(lerp);
        double halfH = height / 2.0;

        List<Vec3> verts = generateConeVertices(halfH, segments, radius, axis);
        modelVertexes.addAll(verts);

        int apexIndex = segments;
        int centerIndex = segments + 1;

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(i);
            indices.add(apexIndex);
            indices.add(next);
            indices.add(i);
            indices.add(next);
            indices.add(centerIndex);
        }

        indexBuffer = indices.stream().mapToInt(Integer::intValue).toArray();
    }

    protected List<Vector3f[]> getLocalConeEdges(boolean lerp) {
        generateRawGeometry(lerp);
        int segments = Math.max(4, getSegments(lerp));
        List<Vector3f[]> edges = new ArrayList<>();
        Vec3 apex = modelVertexes.get(segments);
        Vector3f apexV = new Vector3f((float) apex.x, (float) apex.y, (float) apex.z);

        // Base ring
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 p1 = modelVertexes.get(i);
            Vec3 p2 = modelVertexes.get(next);
            edges.add(new Vector3f[]{
                    new Vector3f((float) p1.x, (float) p1.y, (float) p1.z),
                    new Vector3f((float) p2.x, (float) p2.y, (float) p2.z)
            });
        }
        // Apex lines
        for (int i = 0; i < segments; i++) {
            Vec3 p1 = modelVertexes.get(i);
            edges.add(new Vector3f[]{
                    new Vector3f((float) p1.x, (float) p1.y, (float) p1.z),
                    apexV
            });
        }
        return edges;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        List<Vector3f[]> edges = getLocalConeEdges(false);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = 0.05f;

        for (Vector3f[] edge : edges) {
            Transformation t = DisplayTransformHelper.localSegment(edge[0], edge[1], width, rot, null);
            VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        List<Vector3f[]> edges = getLocalConeEdges(true);
        if (this.displays.size() != edges.size()) {
            ServerLevel lvl = this.displays.isEmpty() ? this.level : this.displays.getFirst().getLevel();
            removeDisplays();
            if (lvl != null) {
                initDisplays(lvl);
            }
            return;
        }

        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = 0.05f;

        VirtualDisplay first = this.displays.getFirst();
        Vec3 spawnPos = new Vec3(first.getEntity().getX(), first.getEntity().getY(), first.getEntity().getZ());
        Vec3 centerOffset = center.subtract(spawnPos);

        for (int i = 0; i < edges.size(); i++) {
            Vector3f[] edge = edges.get(i);
            Transformation t = DisplayTransformHelper.localSegment(edge[0], edge[1], width, rot, centerOffset);
            VirtualDisplay display = this.displays.get(i);
            display.blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }
    }
}
