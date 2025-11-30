package mypals.ml.shape.basics.core;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.tags.DrawableLine;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public interface LineLikeShape extends DrawableLine {
    void setLineWidth(float width);

    float getLineWidth(boolean lerp);

    default void addLineSegment(VertexBuilder vertexBuilder, Vec3 start, Vec3 end) {
        double dx = end.x() - start.x();
        double dy = end.y() - start.y();
        double dz = end.z() - start.z();

        double distanceInv = 1.0 / Math.sqrt(dx * dx + dy * dy + dz * dz);
        Vec3 normal = new Vec3(dx * distanceInv, dy * distanceInv, dz * distanceInv);

        vertexBuilder.putVertex(start, normal);
        vertexBuilder.putVertex(end, normal);
    }

    class SimpleLineTransformer extends DefaultTransformer {
        public LineModelInfo lineModelInfo;

        public SimpleLineTransformer(Shape s, float width, Vec3 center) {
            super(s, center);
            lineModelInfo = new LineModelInfo(width);
        }

        public void setWidth(float width) {
            lineModelInfo.setWidth(width);
        }

        public float getWidth(boolean lerp) {
            return lineModelInfo.getWidth(lerp);
        }

        public void syncLastToTarget() {
            lineModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }

        public boolean asyncModelInfo() {
            return lineModelInfo.async();
        }

        @Override
        public void updateTickDelta(float delta) {
            this.lineModelInfo.update(delta);
            super.updateTickDelta(delta);
        }
    }


    static boolean isSegmentInFrustum(Vec3 a, Vec3 b, Matrix4f mvp) {
        Vector4f ca = new Vector4f((float) a.x, (float) a.y, (float) a.z, 1f);
        Vector4f cb = new Vector4f((float) b.x, (float) b.y, (float) b.z, 1f);

        ca.mul(mvp);
        cb.mul(mvp);

        if (ca.w <= 0 && cb.w <= 0) return false;

        int codeA = computeOutCode(ca);
        int codeB = computeOutCode(cb);

        return (codeA & codeB) == 0;
    }

    private static int computeOutCode(Vector4f c) {
        int code = 0;
        float x = c.x, y = c.y, z = c.z, w = c.w;

        if (x < -w) code |= 1;
        if (x > w) code |= 2;
        if (y < -w) code |= 4;
        if (y > w) code |= 8;
        if (z < -w) code |= 16;
        if (z > w) code |= 32;

        return code;
    }
}
