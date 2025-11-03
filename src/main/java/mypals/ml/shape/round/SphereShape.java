package mypals.ml.shape.round;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.tags.DrawableTriangle;
import mypals.ml.transform.FloatValueTransformer;
import mypals.ml.transform.IntValueTransformer;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static mypals.ml.shape.round.SphereShape.SphereMode.UV;

public class SphereShape extends Shape implements CircleLikeShape, DrawableTriangle {
    public enum SphereMode {
        UV,
        Ico
    }

    public int segments = 180;
    public float radius = 1;
    public SphereMode mode = UV;
    public ArrayList<Vec3> vertexes = new ArrayList<>();
    public Color color = Color.white;

    public SphereShape(RenderingType type, BiConsumer<SphereTransformer, Shape> transform, SphereMode mode, Vec3 center, int segments, float radius, Color color, boolean seeThrough) {
        super(type, seeThrough);
        this.transformer = new SphereTransformer(this, segments, radius);
        this.transformFunction = (defaultTransformer, shape) -> transform.accept((SphereTransformer) this.transformer, shape);
        this.segments = segments;
        this.radius = radius;
        this.color = color;
        ((SphereTransformer) this.transformer).setRadius(this.radius);
        ((SphereTransformer) this.transformer).setSegment(this.segments);
        this.setMode(mode);
        this.centerPoint = center;
        this.transformer.setShapeCenterPos(this.centerPoint);
        syncLastToTarget();
    }
    /*@Override
    public Vec3 calculateShapeCenterPos() {
        if (vertexes.isEmpty()) {
            return Vec3.ZERO;
        }

        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3 vertex : vertexes) {
            sumX += vertex.x;
            sumY += vertex.y;
            sumZ += vertex.z;
        }

        int count = vertexes.size();
        return new Vec3(sumX / count, sumY / count, sumZ / count);
    }*/
    public void generateSphereShape() {
        ArrayList<Vec3> vs = new ArrayList<>();
        Vec3 center = Vec3.ZERO;
        double radius = getRadius();

        switch (mode) {
            case UV -> {
                int latSegments = segments;
                int lonSegments = segments * 2;

                for (int i = 0; i < latSegments; i++) {
                    double theta1 = i * Math.PI / latSegments;
                    double theta2 = (i + 1) * Math.PI / latSegments;

                    for (int j = 0; j < lonSegments; j++) {
                        double phi1 = j * 2 * Math.PI / lonSegments;
                        double phi2 = (j + 1) * 2 * Math.PI / lonSegments;

                        Vec3 v0 = sphericalToCartesian(center, radius, theta1, phi1);
                        Vec3 v1 = sphericalToCartesian(center, radius, theta1, phi2);
                        Vec3 v2 = sphericalToCartesian(center, radius, theta2, phi1);
                        Vec3 v3 = sphericalToCartesian(center, radius, theta2, phi2);

                        vs.add(v0);
                        vs.add(v2);
                        vs.add(v1);

                        vs.add(v1);
                        vs.add(v2);
                        vs.add(v3);
                    }
                }
            }

            case Ico -> {
                IcoSphereGenerator generator = new IcoSphereGenerator(center, radius, segments);
                vs.addAll(generator.getVertexList());
            }

            default -> throw new IllegalStateException("Unexpected mode value: " + mode);
        }

        vertexes = vs;
    }

    private Vec3 sphericalToCartesian(Vec3 center, double r, double theta, double phi) {
        double x = center.x + r * Math.sin(theta) * Math.cos(phi);
        double y = center.y + r * Math.cos(theta);
        double z = center.z + r * Math.sin(theta) * Math.sin(phi);
        return new Vec3(x, y, z);
    }

    public void setMode(SphereMode mode) {
        this.mode = mode;
        generateSphereShape();
    }

    @Override
    public void draw(VertexBuilder builder) {

        builder.putColor(this.color);

        for (Vec3 v : vertexes) {
            builder.putVertex(v.add(getShapeCenterPos()));
        }

    }

    public static class SphereTransformer extends DefaultTransformer {
        public IntValueTransformer segmentTransformer = new IntValueTransformer();
        public FloatValueTransformer radiusTransformer = new FloatValueTransformer();

        public SphereTransformer(Shape managerShape, int seg, float rad) {
            super(managerShape);
            setSegment(seg);
            setRadius(rad);
        }

        public void setSegment(int segment) {
            this.segmentTransformer.setTargetValue(segment);
        }

        public void setRadius(float radius) {
            this.radiusTransformer.setTargetValue(radius);
        }

        @Override
        public void applyTransformations(PoseStack matrixStack) {
            super.applyTransformations(matrixStack);
            float deltaTime = getTickDelta();
            if (this.managedShape instanceof SphereShape shape) {
                this.segmentTransformer.updateValue(shape::setSegments, deltaTime);
                this.radiusTransformer.updateValue(shape::setRadius, deltaTime);
            }
        }

        @Override
        public void syncLastToTarget() {
            this.segmentTransformer.syncLastToTarget();
            this.radiusTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }
    }

    @Override
    public void setRadius(float radius) {
        boolean rebuild = this.radius != radius;
        this.radius = radius;
        if (rebuild) {
            generateSphereShape();
        }

    }

    @Override
    public void setSegments(int segments) {
        boolean rebuild = this.segments != segments;
        this.segments = segments;
        if (rebuild) {
            generateSphereShape();
        }
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public int getSegments() {
        return segments;
    }

    public static class IcoSphereGenerator {

        private final Map<Long, Integer> middlePointCache = new HashMap<>();
        private final ArrayList<Vec3> vertices = new ArrayList<>();
        private final ArrayList<int[]> faces = new ArrayList<>();
        private final Vec3 center;
        private final double radius;
        private final int segments;

        public IcoSphereGenerator(Vec3 center, double radius, int segments) {
            this.center = center;
            this.radius = radius;
            this.segments = segments;
            generateIcosahedron();
            refine();
        }

        private void generateIcosahedron() {
            vertices.clear();
            faces.clear();

            double t = (1.0 + Math.sqrt(5.0)) / 2.0; // 黄金分割
            double s = Math.sqrt(1 + t * t);

            vertices.add(normalize(new Vec3(-1, t, 0), radius));
            vertices.add(normalize(new Vec3(1, t, 0), radius));
            vertices.add(normalize(new Vec3(-1, -t, 0), radius));
            vertices.add(normalize(new Vec3(1, -t, 0), radius));

            vertices.add(normalize(new Vec3(0, -1, t), radius));
            vertices.add(normalize(new Vec3(0, 1, t), radius));
            vertices.add(normalize(new Vec3(0, -1, -t), radius));
            vertices.add(normalize(new Vec3(0, 1, -t), radius));

            vertices.add(normalize(new Vec3(t, 0, -1), radius));
            vertices.add(normalize(new Vec3(t, 0, 1), radius));
            vertices.add(normalize(new Vec3(-t, 0, -1), radius));
            vertices.add(normalize(new Vec3(-t, 0, 1), radius));

            faces.addAll(Arrays.asList(
                    new int[]{0, 11, 5}, new int[]{0, 5, 1}, new int[]{0, 1, 7}, new int[]{0, 7, 10}, new int[]{0, 10, 11},
                    new int[]{1, 5, 9}, new int[]{5, 11, 4}, new int[]{11, 10, 2}, new int[]{10, 7, 6}, new int[]{7, 1, 8},
                    new int[]{3, 9, 4}, new int[]{3, 4, 2}, new int[]{3, 2, 6}, new int[]{3, 6, 8}, new int[]{3, 8, 9},
                    new int[]{4, 9, 5}, new int[]{2, 4, 11}, new int[]{6, 2, 10}, new int[]{8, 6, 7}, new int[]{9, 8, 1}
            ));
        }

        private void refine() {
            for (int i = 0; i < segments; i++) {
                ArrayList<int[]> newFaces = new ArrayList<>();
                for (int[] tri : faces) {
                    int a = tri[0];
                    int b = tri[1];
                    int c = tri[2];

                    int ab = getMiddlePoint(a, b);
                    int bc = getMiddlePoint(b, c);
                    int ca = getMiddlePoint(c, a);

                    newFaces.add(new int[]{a, ab, ca});
                    newFaces.add(new int[]{b, bc, ab});
                    newFaces.add(new int[]{c, ca, bc});
                    newFaces.add(new int[]{ab, bc, ca});
                }
                faces.clear();
                faces.addAll(newFaces);
            }
        }

        private int getMiddlePoint(int p1, int p2) {
            long smallerIndex = Math.min(p1, p2);
            long greaterIndex = Math.max(p1, p2);
            long key = (smallerIndex << 32) + greaterIndex;

            if (middlePointCache.containsKey(key)) {
                return middlePointCache.get(key);
            }

            Vec3 point1 = vertices.get(p1);
            Vec3 point2 = vertices.get(p2);
            Vec3 middle = new Vec3(
                    (point1.x + point2.x) / 2,
                    (point1.y + point2.y) / 2,
                    (point1.z + point2.z) / 2
            );

            middle = normalize(middle, radius);

            vertices.add(middle);
            int index = vertices.size() - 1;
            middlePointCache.put(key, index);
            return index;
        }

        private Vec3 normalize(Vec3 v, double r) {
            double length = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
            return new Vec3(
                    center.x + v.x / length * r,
                    center.y + v.y / length * r,
                    center.z + v.z / length * r
            );
        }

        public ArrayList<Vec3> getVertexList() {
            ArrayList<Vec3> result = new ArrayList<>();
            for (int[] tri : faces) {
                result.add(vertices.get(tri[0]));
                result.add(vertices.get(tri[1]));
                result.add(vertices.get(tri[2]));
            }
            return result;
        }
    }
}