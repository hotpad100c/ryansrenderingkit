package ml.mypals.ryansserverrenderingkit.shape.round;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.DrawableTriangle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SphereShape extends Shape implements CircleLikeShape, DrawableTriangle {

    public SphereShape(Consumer<FaceCircleShape.FaceCircleTransformer> transform,
                       Vec3 center, int segments, float radius, Color color, boolean seeThrough) {
        super(color, seeThrough);
        this.transformer = new FaceCircleShape.FaceCircleTransformer(this, segments, radius, center);
        this.transformFunction = (defaultTransformer) -> {
            if (transform != null) {
                transform.accept((FaceCircleShape.FaceCircleTransformer) this.transformer);
            }
        };
        syncLastToTarget();
        generateSphereShape(true);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        generateSphereShape(lerp);
    }

    public void generateSphereShape(boolean lerp) {
        ArrayList<Vec3> vs = new ArrayList<>();
        Vec3 center = Vec3.ZERO;
        double radius = getRadius(lerp);
        int segments = Math.max(4, getSegments(lerp));

        int lonSegments = segments * 2;
        Vec3[][] vertexGrid = new Vec3[segments + 1][lonSegments + 1];

        for (int i = 0; i <= segments; i++) {
            double theta = i * Math.PI / segments;
            for (int j = 0; j <= lonSegments; j++) {
                double phi = j * 2 * Math.PI / lonSegments;
                vertexGrid[i][j] = sphericalToCartesian(center, radius, theta, phi);
                vs.add(vertexGrid[i][j]);
            }
        }

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < lonSegments; j++) {
                int v0 = i * (lonSegments + 1) + j;
                int v1 = (i + 1) * (lonSegments + 1) + j;
                int v2 = i * (lonSegments + 1) + (j + 1);
                int v3 = (i + 1) * (lonSegments + 1) + (j + 1);

                indices.add(v0);
                indices.add(v1);
                indices.add(v2);
                indices.add(v2);
                indices.add(v1);
                indices.add(v3);
            }
        }

        indexBuffer = indices.stream().mapToInt(Integer::intValue).toArray();
        modelVertexes = vs;
    }

    private Vec3 sphericalToCartesian(Vec3 center, double r, double theta, double phi) {
        double x = center.x + r * Math.sin(theta) * Math.cos(phi);
        double y = center.y + r * Math.cos(theta);
        double z = center.z + r * Math.sin(theta) * Math.sin(phi);
        return new Vec3(x, y, z);
    }

    private List<Vector3f[]> getSphereRingEdges(boolean lerp) {
        float radius = getRadius(lerp);
        int ringSegs = Math.max(8, Math.min(24, getSegments(lerp)));

        List<Vector3f[]> edges = new ArrayList<>();

        for (int axis = 0; axis < 3; axis++) {
            for (int i = 0; i < ringSegs; i++) {
                double theta1 = 2 * Math.PI * i / ringSegs;
                double theta2 = 2 * Math.PI * (i + 1) / ringSegs;

                double x1 = 0, y1 = 0, z1 = 0;
                double x2 = 0, y2 = 0, z2 = 0;

                if (axis == 0) { // XY plane
                    x1 = radius * Math.cos(theta1); y1 = radius * Math.sin(theta1);
                    x2 = radius * Math.cos(theta2); y2 = radius * Math.sin(theta2);
                } else if (axis == 1) { // YZ plane
                    y1 = radius * Math.cos(theta1); z1 = radius * Math.sin(theta1);
                    y2 = radius * Math.cos(theta2); z2 = radius * Math.sin(theta2);
                } else { // XZ plane
                    x1 = radius * Math.cos(theta1); z1 = radius * Math.sin(theta1);
                    x2 = radius * Math.cos(theta2); z2 = radius * Math.sin(theta2);
                }

                Vector3f va = new Vector3f((float) x1, (float) y1, (float) z1);
                Vector3f vb = new Vector3f((float) x2, (float) y2, (float) z2);
                edges.add(new Vector3f[]{va, vb});
            }
        }
        return edges;
    }

    private List<Transformation> getSpherePanels(boolean lerp, Vec3 centerOffset) {
        float radius = getRadius(lerp);
        Quaternionf rotation = transformer.getWorldRotation();
        float goldenRatio = (1.0F + (float) Math.sqrt(5.0D)) * 0.5F;
        Vector3f[] vertices = {
                spherePoint(-1, goldenRatio, 0, radius), spherePoint(1, goldenRatio, 0, radius),
                spherePoint(-1, -goldenRatio, 0, radius), spherePoint(1, -goldenRatio, 0, radius),
                spherePoint(0, -1, goldenRatio, radius), spherePoint(0, 1, goldenRatio, radius),
                spherePoint(0, -1, -goldenRatio, radius), spherePoint(0, 1, -goldenRatio, radius),
                spherePoint(goldenRatio, 0, -1, radius), spherePoint(goldenRatio, 0, 1, radius),
                spherePoint(-goldenRatio, 0, -1, radius), spherePoint(-goldenRatio, 0, 1, radius)
        };
        int[][] faceIndices = {
                {0, 11, 5}, {0, 5, 1}, {0, 1, 7}, {0, 7, 10}, {0, 10, 11},
                {1, 5, 9}, {5, 11, 4}, {11, 10, 2}, {10, 7, 6}, {7, 1, 8},
                {3, 9, 4}, {3, 4, 2}, {3, 2, 6}, {3, 6, 8}, {3, 8, 9},
                {4, 9, 5}, {2, 4, 11}, {6, 2, 10}, {8, 6, 7}, {9, 8, 1}
        };
        List<Vector3f[]> triangles = new ArrayList<>(20);
        for (int[] face : faceIndices) {
            triangles.add(new Vector3f[]{vertices[face[0]], vertices[face[1]], vertices[face[2]]});
        }

        // ponytail: cap at one subdivision (80 faces / 240 displays); raise only after profiling.
        if (getSegments(lerp) >= 10) {
            List<Vector3f[]> subdivided = new ArrayList<>(80);
            for (Vector3f[] triangle : triangles) {
                Vector3f ab = spherePoint(new Vector3f(triangle[0]).add(triangle[1]), radius);
                Vector3f bc = spherePoint(new Vector3f(triangle[1]).add(triangle[2]), radius);
                Vector3f ca = spherePoint(new Vector3f(triangle[2]).add(triangle[0]), radius);
                subdivided.add(new Vector3f[]{triangle[0], ab, ca});
                subdivided.add(new Vector3f[]{triangle[1], bc, ab});
                subdivided.add(new Vector3f[]{triangle[2], ca, bc});
                subdivided.add(new Vector3f[]{ab, bc, ca});
            }
            triangles = subdivided;
        }

        List<Transformation> panels = new ArrayList<>();
        for (Vector3f[] triangle : triangles) {
            panels.addAll(DisplayTransformHelper.localTextTriangle(
                    triangle[0], triangle[1], triangle[2], rotation, centerOffset));
        }
        return panels;
    }

    private Vector3f spherePoint(float x, float y, float z, float radius) {
        return spherePoint(new Vector3f(x, y, z), radius);
    }

    private Vector3f spherePoint(Vector3f point, float radius) {
        return point.normalize().mul(radius);
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 center = transformer.getWorldPivot();

        if (renderFace) {
            for (Transformation panel : getSpherePanels(false, null)) {
                this.displays.add(VirtualDisplay.solidTextPanel(
                                level, center.x, center.y, center.z, this.baseColor.getRGB())
                        .bright()
                        .seeThrough(this.seeThrough, this.baseColor.getRGB())
                        .transform(panel));
            }
        }
        if (renderWireframe) {
            Quaternionf rotation = transformer.getWorldRotation();
            for (Vector3f[] edge : getSphereRingEdges(false)) {
                this.displays.add(VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                        .bright()
                        .seeThrough(this.seeThrough, this.baseColor.getRGB())
                        .transform(DisplayTransformHelper.localSegment(
                                edge[0], edge[1], wireframeWidth, rotation, null)));
            }
        }
    }

    @Override
    public void updateDisplays() {
        Vec3 center = transformer.getWorldPivot();
        Vec3 centerOffset = this.displays.isEmpty()
                ? null
                : center.subtract(new Vec3(this.displays.getFirst().getEntity().getX(),
                        this.displays.getFirst().getEntity().getY(), this.displays.getFirst().getEntity().getZ()));
        List<Transformation> panels = renderFace ? getSpherePanels(true, centerOffset) : List.of();
        List<Vector3f[]> edges = renderWireframe ? getSphereRingEdges(true) : List.of();
        if (this.displays.size() != panels.size() + edges.size()) {
            ServerLevel lvl = this.displays.isEmpty() ? this.level : this.displays.getFirst().getLevel();
            removeDisplays();
            if (lvl != null) {
                initDisplays(lvl);
            }
            return;
        }
        int displayIndex = 0;
        for (Transformation panel : panels) {
            this.displays.get(displayIndex++)
                    .backgroundColor(this.baseColor.getRGB())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(panel);
        }
        Quaternionf rotation = transformer.getWorldRotation();
        for (Vector3f[] edge : edges) {
            this.displays.get(displayIndex++)
                    .blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(DisplayTransformHelper.localSegment(
                            edge[0], edge[1], wireframeWidth, rotation, centerOffset));
        }
    }

    @Override
    public void setRadius(float radius) {
        ((FaceCircleShape.FaceCircleTransformer) this.transformer).setRadius(radius);
    }

    @Override
    public void setSegments(int segments) {
        ((FaceCircleShape.FaceCircleTransformer) this.transformer).setSegment(segments);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((FaceCircleShape.FaceCircleTransformer) this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((FaceCircleShape.FaceCircleTransformer) this.transformer).getSegment(lerp);
    }
}
