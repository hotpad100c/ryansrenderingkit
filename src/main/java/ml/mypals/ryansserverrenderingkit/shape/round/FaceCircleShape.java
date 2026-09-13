package ml.mypals.ryansserverrenderingkit.shape.round;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer.CircleModelInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FaceCircleShape extends Shape implements CircleLikeShape {

    public CircleAxis axis = CircleAxis.X;

    public FaceCircleShape(Consumer<FaceCircleTransformer> transform,
                           CircleAxis axis,
                           Vec3 center,
                           int segments,
                           float radius,
                           Color color,
                           boolean seeThrough) {
        super(color, seeThrough);
        this.axis = axis != null ? axis : CircleAxis.X;

        this.transformer = new FaceCircleTransformer(this, segments, radius, center);
        this.transformFunction = (t) -> {
            if (transform != null) {
                transform.accept((FaceCircleTransformer) this.transformer);
            }
        };

        generateRawGeometry(true);
        syncLastToTarget();
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        if (this.getSegments(lerp) < 3) return;

        int segments = getSegments(lerp);
        float radius = getRadius(lerp);

        modelVertexes.add(Vec3.ZERO);

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double x = 0, y = 0, z = 0;

            switch (axis) {
                case X -> {
                    y = radius * Math.cos(theta);
                    z = radius * Math.sin(theta);
                    x = 0;
                }
                case Y -> {
                    x = radius * Math.cos(theta);
                    z = radius * Math.sin(theta);
                    y = 0;
                }
                case Z -> {
                    x = radius * Math.cos(theta);
                    y = radius * Math.sin(theta);
                    z = 0;
                }
            }
            modelVertexes.add(new Vec3(x, y, z));
        }

        indexBuffer = new int[segments * 3];
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indexBuffer[i * 3] = 0;
            indexBuffer[i * 3 + 1] = i + 1;
            indexBuffer[i * 3 + 2] = next + 1;
        }
    }

    private List<Vec3> getWorldPerimeter(boolean lerp) {
        generateRawGeometry(lerp);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();

        List<Vec3> result = new ArrayList<>();
        Vector3f v = new Vector3f();
        // modelVertexes.get(0) is center, 1..n are perimeter
        for (int i = 1; i < modelVertexes.size(); i++) {
            Vec3 local = modelVertexes.get(i);
            v.set((float) local.x, (float) local.y, (float) local.z);
            rot.transform(v);
            result.add(new Vec3(center.x + v.x, center.y + v.y, center.z + v.z));
        }
        return result;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        generateRawGeometry(false);
        int total = modelVertexes.size();
        if (total < 4) return; // 1 center + at least 3 perimeter

        int n = total - 1;
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = 0.05f;

        // Perimeter segments
        for (int i = 0; i < n; i++) {
            Vec3 p1 = modelVertexes.get(1 + i);
            Vec3 p2 = modelVertexes.get(1 + (i + 1) % n);
            Vector3f va = new Vector3f((float) p1.x, (float) p1.y, (float) p1.z);
            Vector3f vb = new Vector3f((float) p2.x, (float) p2.y, (float) p2.z);
            Transformation t = DisplayTransformHelper.localSegment(va, vb, width, rot, null);

            VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
            this.displays.add(display);
        }

        // Spokes from center to perimeter
        Vector3f centerLocal = new Vector3f(0, 0, 0);
        for (int i = 0; i < n; i += 2) {
            Vec3 p = modelVertexes.get(1 + i);
            Vector3f vb = new Vector3f((float) p.x, (float) p.y, (float) p.z);
            Transformation t = DisplayTransformHelper.localSegment(centerLocal, vb, width, rot, null);

            VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        generateRawGeometry(true);
        int total = modelVertexes.size();
        if (total < 4) return;

        int n = total - 1;
        int expected = n + (n + 1) / 2;
        if (this.displays.size() != expected) {
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

        int idx = 0;
        for (int i = 0; i < n; i++) {
            Vec3 p1 = modelVertexes.get(1 + i);
            Vec3 p2 = modelVertexes.get(1 + (i + 1) % n);
            Vector3f va = new Vector3f((float) p1.x, (float) p1.y, (float) p1.z);
            Vector3f vb = new Vector3f((float) p2.x, (float) p2.y, (float) p2.z);
            Transformation t = DisplayTransformHelper.localSegment(va, vb, width, rot, centerOffset);
            VirtualDisplay display = this.displays.get(idx++);
            display.blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }

        Vector3f centerLocal = new Vector3f(0, 0, 0);
        for (int i = 0; i < n; i += 2) {
            Vec3 p = modelVertexes.get(1 + i);
            Vector3f vb = new Vector3f((float) p.x, (float) p.y, (float) p.z);
            Transformation t = DisplayTransformHelper.localSegment(centerLocal, vb, width, rot, centerOffset);
            VirtualDisplay display = this.displays.get(idx++);
            display.blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }
    }

    @Override
    public void setRadius(float radius) {
        ((FaceCircleTransformer) this.transformer).setRadius(radius);
    }

    @Override
    public void setSegments(int segments) {
        ((FaceCircleTransformer) this.transformer).setSegment(segments);
    }

    public void forceSetRadius(float radius) {
        setRadius(radius);
        ((FaceCircleTransformer) this.transformer).circleModelInfo.radiusTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetSegments(int segments) {
        setSegments(segments);
        ((FaceCircleTransformer) this.transformer).circleModelInfo.segmentTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((FaceCircleTransformer) this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((FaceCircleTransformer) this.transformer).getSegment(lerp);
    }

    public static class FaceCircleTransformer extends DefaultTransformer {
        public final CircleModelInfo circleModelInfo;

        public FaceCircleTransformer(Shape managedShape, int segments, float radius, Vec3 center) {
            super(managedShape, center);
            this.circleModelInfo = new CircleModelInfo(segments, radius);
        }

        public void setSegment(int segments) { circleModelInfo.setSegment(segments); }
        public void setRadius(float radius)   { circleModelInfo.setRadius(radius); }
        public int getSegment(boolean lerp)   { return circleModelInfo.getSegment(lerp); }
        public float getRadius(boolean lerp)  { return circleModelInfo.getRadius(lerp); }

        @Override
        public void updateTickDelta(float delta) {
            circleModelInfo.update(delta);
            super.updateTickDelta(delta);
        }

        @Override
        public void syncLastToTarget() {
            circleModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }

        public boolean asyncModelInfo() {
            return circleModelInfo.async();
        }
    }
}
