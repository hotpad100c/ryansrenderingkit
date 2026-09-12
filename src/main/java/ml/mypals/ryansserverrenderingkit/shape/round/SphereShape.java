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

    private List<Vec3[]> getSphereRingEdges(boolean lerp) {
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float radius = getRadius(lerp);
        int ringSegs = Math.max(8, Math.min(24, getSegments(lerp)));

        List<Vec3[]> edges = new ArrayList<>();
        Vector3f va = new Vector3f();
        Vector3f vb = new Vector3f();

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

                va.set((float) x1, (float) y1, (float) z1);
                vb.set((float) x2, (float) y2, (float) z2);
                rot.transform(va);
                rot.transform(vb);

                Vec3 a = new Vec3(center.x + va.x, center.y + va.y, center.z + va.z);
                Vec3 b = new Vec3(center.x + vb.x, center.y + vb.y, center.z + vb.z);
                edges.add(new Vec3[]{a, b});
            }
        }
        return edges;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        List<Vec3[]> edges = getSphereRingEdges(false);
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
        List<Vec3[]> edges = getSphereRingEdges(true);
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