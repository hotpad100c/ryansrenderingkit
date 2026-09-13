package ml.mypals.ryansserverrenderingkit.collision;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RayModelIntersection {
    private static final double EPSILON = 1e-6;


    public static class Ray {
        public Vec3 origin;
        public Vec3 direction;

        public Ray(Vec3 origin, Vec3 direction) {
            this.origin = origin;
            this.direction = direction.normalize();
        }
    }

    public static class HitResult {
        public boolean hit;
        @Nullable
        public Vec3 pos;
        public double distance;

        public HitResult(boolean hit, @Nullable Vec3 pos, double distance) {
            this.hit = hit;
            this.pos = pos;
            this.distance = distance;
        }
    }

    public static boolean intersectTriangle(Ray ray, Vec3 v0, Vec3 v1, Vec3 v2, double[] outT) {


        double e1x = v1.x - v0.x, e1y = v1.y - v0.y, e1z = v1.z - v0.z;
        double e2x = v2.x - v0.x, e2y = v2.y - v0.y, e2z = v2.z - v0.z;
        double px = ray.direction.y * e2z - ray.direction.z * e2y;
        double py = ray.direction.z * e2x - ray.direction.x * e2z;
        double pz = ray.direction.x * e2y - ray.direction.y * e2x;
        double det = e1x * px + e1y * py + e1z * pz;

        if (Math.abs(det) < EPSILON) return false;

        double invDet = 1.0 / det;
        double tx = ray.origin.x - v0.x, ty = ray.origin.y - v0.y, tz = ray.origin.z - v0.z;

        double u = (tx * px + ty * py + tz * pz) * invDet;
        if (u < 0.0 || u > 1.0) return false;

        double qx = ty * e1z - tz * e1y;
        double qy = tz * e1x - tx * e1z;
        double qz = tx * e1y - ty * e1x;
        double v = (ray.direction.x * qx + ray.direction.y * qy + ray.direction.z * qz) * invDet;
        if (v < 0.0 || u + v > 1.0) return false;

        double t = (e2x * qx + e2y * qy + e2z * qz) * invDet;
        if (t < EPSILON) return false;

        if (outT != null) outT[0] = t;
        return true;
    }

    public static HitResult rayIntersectsModel(
            Ray ray,
            List<Vec3> modelVertexes,
            int[] indexBuffer) {
        return new PreparedModel(modelVertexes, indexBuffer).intersect(ray, -1);
    }

    public static final class PreparedModel {
        private final List<Vec3> vertices;
        private final int[] indices;
        private final double[] min = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        private final double[] max = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};

        public PreparedModel(List<Vec3> vertices, int[] indices) {
            this.vertices = vertices;
            this.indices = indices;
            for (Vec3 v : vertices) {
                min[0] = Math.min(min[0], v.x); max[0] = Math.max(max[0], v.x);
                min[1] = Math.min(min[1], v.y); max[1] = Math.max(max[1], v.y);
                min[2] = Math.min(min[2], v.z); max[2] = Math.max(max[2], v.z);
            }
        }

        private boolean intersectsBounds(Ray ray, double range) {
            double near = 0, far = range < 0 ? Double.POSITIVE_INFINITY : range;
            for (int axis = 0; axis < 3; axis++) {
                double origin = axis == 0 ? ray.origin.x : axis == 1 ? ray.origin.y : ray.origin.z;
                double direction = axis == 0 ? ray.direction.x : axis == 1 ? ray.direction.y : ray.direction.z;
                double lo = min[axis] - EPSILON, hi = max[axis] + EPSILON;
                if (direction == 0) {
                    if (origin < lo || origin > hi) return false;
                } else {
                    double a = (lo - origin) / direction, b = (hi - origin) / direction;
                    near = Math.max(near, Math.min(a, b));
                    far = Math.min(far, Math.max(a, b));
                    if (near > far) return false;
                }
            }
            return true;
        }

        public HitResult intersect(Ray ray, double range) {
            if (vertices.isEmpty() || indices.length % 3 != 0 || !intersectsBounds(ray, range)) {
                return new HitResult(false, null, -1);
            }
            return intersectTriangles(ray, vertices, indices, range);
        }
    }

    private static HitResult intersectTriangles(Ray ray, List<Vec3> modelVertexes, int[] indexBuffer, double range) {

        double closestT = Double.POSITIVE_INFINITY;
        boolean hit = false;
        double[] tOut = new double[1];

        for (int i = 0; i < indexBuffer.length; i += 3) {
            int i0 = indexBuffer[i];
            int i1 = indexBuffer[i + 1];
            int i2 = indexBuffer[i + 2];
            if (i0 < 0 || i1 < 0 || i2 < 0 || i0 >= modelVertexes.size() || i1 >= modelVertexes.size() || i2 >= modelVertexes.size()) {
                continue;
            }

            Vec3 v0 = modelVertexes.get(i0);
            Vec3 v1 = modelVertexes.get(i1);
            Vec3 v2 = modelVertexes.get(i2);

            if (intersectTriangle(ray, v0, v1, v2, tOut)) {
                double t = tOut[0];
                if (t < closestT && (range < 0 || t <= range)) {
                    closestT = t;
                    hit = true;
                }
            }
        }

        if (!hit) {
            return new HitResult(hit, null, -1);
        }

        Vec3 hitPoint = new Vec3(
                (float) (ray.origin.x + ray.direction.x * closestT),
                (float) (ray.origin.y + ray.direction.y * closestT),
                (float) (ray.origin.z + ray.direction.z * closestT)
        );
        return new HitResult(hit, hitPoint, closestT);
    }
}
