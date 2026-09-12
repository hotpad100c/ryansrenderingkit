package ml.mypals.ryansserverrenderingkit.shape.round;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.basics.core.LineLikeShape;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer.CircleModelInfo;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LineCircleShape extends Shape implements CircleLikeShape, LineLikeShape {

    public CircleAxis axis = CircleAxis.X;

    public LineCircleShape(Consumer<LineCircleTransformer> transform,
                           CircleAxis axis,
                           Vec3 center,
                           int segments,
                           float radius,
                           float width,
                           Color color,
                           boolean seeThrough) {
        super(color, seeThrough);
        this.axis = axis != null ? axis : CircleAxis.X;

        this.transformer = new LineCircleTransformer(this, segments, radius, width, center);
        this.transformFunction = t -> {
            if (transform != null) {
                transform.accept((LineCircleTransformer) this.transformer);
            }
        };

        generateRawGeometry(true);
        syncLastToTarget();
    }

    @Deprecated
    public LineCircleShape(Object ignored,
                           Consumer<LineCircleTransformer> transform,
                           CircleAxis axis,
                           Vec3 center,
                           int segments,
                           float radius,
                           float width,
                           Color color,
                           boolean seeThrough) {
        this(transform, axis, center, segments, radius, width, color, seeThrough);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        int segments = getSegments(lerp);
        float radius = getRadius(lerp);
        if (segments < 3) return;

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

        indexBuffer = new int[segments * 2];
        for (int i = 0; i < segments; i++) {
            indexBuffer[i * 2] = i;
            indexBuffer[i * 2 + 1] = (i + 1) % segments;
        }
    }

    private List<Vec3> getWorldVertices(boolean lerp) {
        generateRawGeometry(lerp);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();

        List<Vec3> result = new ArrayList<>(modelVertexes.size());
        Vector3f v = new Vector3f();
        for (Vec3 local : modelVertexes) {
            v.set((float) local.x, (float) local.y, (float) local.z);
            rot.transform(v);
            result.add(new Vec3(center.x + v.x, center.y + v.y, center.z + v.z));
        }
        return result;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        generateRawGeometry(false);
        int n = modelVertexes.size();
        if (n < 3) return;

        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = getLineWidth(false);

        for (int i = 0; i < n; i++) {
            Vec3 p1 = modelVertexes.get(i);
            Vec3 p2 = modelVertexes.get((i + 1) % n);
            Vector3f va = new Vector3f((float) p1.x, (float) p1.y, (float) p1.z);
            Vector3f vb = new Vector3f((float) p2.x, (float) p2.y, (float) p2.z);
            Transformation t = DisplayTransformHelper.localSegment(va, vb, width, rot, null);

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
        int n = modelVertexes.size();
        if (this.displays.size() != n) {
            ServerLevel lvl = this.displays.isEmpty() ? this.level : this.displays.getFirst().getLevel();
            removeDisplays();
            if (lvl != null) {
                initDisplays(lvl);
            }
            return;
        }

        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = getLineWidth(true);

        VirtualDisplay first = this.displays.getFirst();
        Vec3 spawnPos = new Vec3(first.getEntity().getX(), first.getEntity().getY(), first.getEntity().getZ());
        Vec3 centerOffset = center.subtract(spawnPos);

        for (int i = 0; i < n; i++) {
            Vec3 p1 = modelVertexes.get(i);
            Vec3 p2 = modelVertexes.get((i + 1) % n);
            Vector3f va = new Vector3f((float) p1.x, (float) p1.y, (float) p1.z);
            Vector3f vb = new Vector3f((float) p2.x, (float) p2.y, (float) p2.z);
            Transformation t = DisplayTransformHelper.localSegment(va, vb, width, rot, centerOffset);
            VirtualDisplay display = this.displays.get(i);
            display.blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }
    }

    @Override
    public void setRadius(float radius) {
        ((LineCircleTransformer) this.transformer).setRadius(radius);
    }

    public void forceSetRadius(float radius) {
        setRadius(radius);
        ((LineCircleTransformer) this.transformer).circleModelInfo.radiusTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetSegments(int segments) {
        setSegments(segments);
        ((LineCircleTransformer) this.transformer).circleModelInfo.segmentTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((LineCircleTransformer) this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    @Override
    public void setSegments(int segments) {
        ((LineCircleTransformer) this.transformer).setSegment(segments);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((LineCircleTransformer) this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((LineCircleTransformer) this.transformer).getSegment(lerp);
    }

    @Override
    public void setLineWidth(float width) {
        ((LineCircleTransformer) this.transformer).setWidth(width);
    }

    @Override
    public float getLineWidth(boolean lerp) {
        return ((LineCircleTransformer) this.transformer).getWidth(lerp);
    }

    public static class LineCircleTransformer extends DefaultTransformer {
        public final CircleModelInfo circleModelInfo;
        public final LineModelInfo lineModelInfo;

        public LineCircleTransformer(Shape managedShape, int segments, float radius, float width, Vec3 center) {
            super(managedShape, center);
            circleModelInfo = new CircleModelInfo(segments, radius);
            lineModelInfo = new LineModelInfo(width);
        }

        public void setSegment(int segments) { circleModelInfo.setSegment(segments); }
        public void setRadius(float radius)   { circleModelInfo.setRadius(radius); }
        public void setWidth(float width)     { lineModelInfo.setWidth(width); }
        public float getWidth(boolean lerp)   { return lineModelInfo.getWidth(lerp); }
        public int getSegment(boolean lerp)   { return circleModelInfo.getSegment(lerp); }
        public float getRadius(boolean lerp)  { return circleModelInfo.getRadius(lerp); }

        @Override
        public void updateTickDelta(float delta) {
            circleModelInfo.update(delta);
            lineModelInfo.update(delta);
            super.updateTickDelta(delta);
        }

        @Override
        public void syncLastToTarget() {
            circleModelInfo.syncLastToTarget();
            lineModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }

        public boolean asyncModelInfo() {
            return circleModelInfo.async() || lineModelInfo.async();
        }
    }
}
