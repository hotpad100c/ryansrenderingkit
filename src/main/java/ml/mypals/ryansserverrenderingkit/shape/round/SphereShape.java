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

    @Deprecated
    public SphereShape(Object ignored,
                       Consumer<FaceCircleShape.FaceCircleTransformer> transform,
                       Vec3 center, int segments, float radius, Color color, boolean seeThrough) {
        this(transform, center, segments, radius, color, seeThrough);
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

    @Override
    public void initDisplays(ServerLevel level) {
        List<Vector3f[]> edges = getSphereRingEdges(false);
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
        List<Vector3f[]> edges = getSphereRingEdges(true);
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