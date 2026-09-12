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

    protected List<Vec3[]> getConeEdges(boolean lerp) {
        generateRawGeometry(lerp);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();

        int segments = Math.max(4, getSegments(lerp));
        List<Vec3> worldVerts = new ArrayList<>(modelVertexes.size());
        Vector3f v = new Vector3f();
        for (Vec3 local : modelVertexes) {
            v.set((float) local.x, (float) local.y, (float) local.z);
            rot.transform(v);
            worldVerts.add(new Vec3(center.x + v.x, center.y + v.y, center.z + v.z));
        }

        List<Vec3[]> edges = new ArrayList<>();
        Vec3 apex = worldVerts.get(segments);

        // Base ring
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            edges.add(new Vec3[]{worldVerts.get(i), worldVerts.get(next)});
        }
        // Apex lines
        for (int i = 0; i < segments; i++) {
            edges.add(new Vec3[]{worldVerts.get(i), apex});
        }
        return edges;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        List<Vec3[]> edges = getConeEdges(false);
        float width = 0.05f;
        for (Vec3[] edge : edges) {
            VirtualDisplay display = VirtualDisplay.block(level, edge[0].x, edge[0].y, edge[0].z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(DisplayTransformHelper.segment(edge[0], edge[1], width));
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        List<Vec3[]> edges = getConeEdges(true);
        if (this.displays.size() != edges.size()) {
            ServerLevel lvl = this.displays.isEmpty() ? this.level : this.displays.getFirst().getLevel();
            removeDisplays();
            if (lvl != null) {
                initDisplays(lvl);
            }
            return;
        }

        float width = 0.05f;
        for (int i = 0; i < edges.size(); i++) {
            Vec3[] edge = edges.get(i);
            Transformation t = DisplayTransformHelper.segment(edge[0], edge[1], width);
            VirtualDisplay display = this.displays.get(i);
            display.pos(edge[0].x, edge[0].y, edge[0].z)
                    .blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }
    }
}
