package mypals.ml.collision;

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
        

        Vec3 e1 = v1.subtract(v0);
        Vec3 e2 = v2.subtract(v0);
        Vec3 P = ray.direction.cross(e2);
        double det = e1.dot(P);

        if (Math.abs(det) < EPSILON) return false;

        double invDet = 1.0 / det;
        Vec3 T = ray.origin.subtract(v0);

        double u = T.dot(P) * invDet;
        if (u < 0.0 || u > 1.0) return false;

        Vec3 Q = T.cross(e1);
        double v = ray.direction.dot(Q) * invDet;
        if (v < 0.0 || u + v > 1.0) return false;

        double t = e2.dot(Q) * invDet;
        if (t < EPSILON) return false;

        if (outT != null) outT[0] = t;
        return true;
    }

    public static HitResult rayIntersectsModel(
            Ray ray,
            List<Vec3> modelVertexes,
            int[] indexBuffer) {

        double closestT = Double.POSITIVE_INFINITY;
        Vec3 closestHitVec3d = null;
        boolean hit = false;

        for (int i = 0; i < indexBuffer.length; i += 3) {
            int i0 = indexBuffer[i];
            int i1 = indexBuffer[i + 1];
            int i2 = indexBuffer[i + 2];
            if (i0 >= modelVertexes.size() || i1 >= modelVertexes.size() || i2 >= modelVertexes.size()) {
                continue;
            }

            Vec3 v0 = modelVertexes.get(i0);
            Vec3 v1 = modelVertexes.get(i1);
            Vec3 v2 = modelVertexes.get(i2);

            double[] tOut = new double[1];
            if (intersectTriangle(ray, v0, v1, v2, tOut)) {
                double t = tOut[0];
                if (t < closestT) {
                    closestT = t;
                    closestHitVec3d = ray.origin.add(ray.direction.scale(t));
                    hit = true;
                }
            }
        }

        if (!hit) {
            return new HitResult(hit,null, -1);
        }

        Vec3 hitPoint = new Vec3(
                (float) closestHitVec3d.x,
                (float) closestHitVec3d.y,
                (float) closestHitVec3d.z
        );
        return new HitResult(hit,hitPoint, closestT);
    }
}