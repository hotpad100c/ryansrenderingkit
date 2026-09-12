package ml.mypals.ryansserverrenderingkit.shape.cylinder;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.CircleLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.DrawableTriangle;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.DefaultTransformer;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer.CircleModelInfo;
import ml.mypals.ryansserverrenderingkit.transform.valueTransformers.FloatTransformer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CylinderShape extends Shape implements CircleLikeShape, DrawableTriangle {

    public CircleAxis axis = CircleAxis.X;
    public Color color = Color.WHITE;

    public CylinderShape(Consumer<CylinderTransformer> transform,
                         CircleAxis circleAxis, Vec3 center, int segments,
                         float radius, float height, Color color, boolean seeThrough) {
        super(shape -> {}, color, center, seeThrough);

        this.transformer = new CylinderTransformer(this, segments, radius, height, center);
        this.transformFunction = t -> {
            if (transform != null) {
                transform.accept((CylinderTransformer) this.transformer);
            }
        };
        this.axis = circleAxis != null ? circleAxis : CircleAxis.X;
        generateRawGeometry(false);
        syncLastToTarget();
    }

    public CylinderShape(boolean seeThrough) {
        super(Color.WHITE, seeThrough);
    }

    @Deprecated
    public CylinderShape(Object ignored, Consumer<CylinderTransformer> transform,
                         CircleAxis circleAxis, Vec3 center, int segments,
                         float radius, float height, Color color, boolean seeThrough) {
        this(transform, circleAxis, center, segments, radius, height, color, seeThrough);
    }

    void generateCylinderVertices(boolean lerp) {
        modelVertexes.clear();

        float height = getHeight(lerp);
        float radius = getRadius(lerp);
        int segments = Math.max(4, getSegments(lerp));
        double halfH = height / 2.0;

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            Vec3 bottom = switch (axis) {
                case X -> new Vec3(-halfH, radius * c, radius * s);
                case Y -> new Vec3(radius * c, -halfH, radius * s);
                case Z -> new Vec3(radius * c, radius * s, -halfH);
            };
            modelVertexes.add(bottom);
        }

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            Vec3 top = switch (axis) {
                case X -> new Vec3(halfH, radius * c, radius * s);
                case Y -> new Vec3(radius * c, halfH, radius * s);
                case Z -> new Vec3(radius * c, radius * s, halfH);
            };
            modelVertexes.add(top);
        }
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        generateCylinderVertices(lerp);

        int segments = getSegments(lerp);
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            int b1 = i;
            int b2 = next;
            int t1 = segments + i;
            int t2 = segments + next;

            indices.add(b1);
            indices.add(t1);
            indices.add(b2);

            indices.add(b2);
            indices.add(t1);
            indices.add(t2);
        }

        indexBuffer = indices.stream().mapToInt(Integer::intValue).toArray();
    }

    protected List<Vec3[]> getCylinderEdges(boolean lerp) {
        generateCylinderVertices(lerp);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();

        int segments = getSegments(lerp);
        List<Vec3> worldVerts = new ArrayList<>(modelVertexes.size());
        Vector3f v = new Vector3f();
        for (Vec3 local : modelVertexes) {
            v.set((float) local.x, (float) local.y, (float) local.z);
            rot.transform(v);
            worldVerts.add(new Vec3(center.x + v.x, center.y + v.y, center.z + v.z));
        }

        List<Vec3[]> edges = new ArrayList<>();
        // Bottom ring
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            edges.add(new Vec3[]{worldVerts.get(i), worldVerts.get(next)});
        }
        // Top ring
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            edges.add(new Vec3[]{worldVerts.get(segments + i), worldVerts.get(segments + next)});
        }
        // Side connectors
        for (int i = 0; i < segments; i++) {
            edges.add(new Vec3[]{worldVerts.get(i), worldVerts.get(segments + i)});
        }
        return edges;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        List<Vec3[]> edges = getCylinderEdges(false);
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
        List<Vec3[]> edges = getCylinderEdges(true);
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

    public void setHeight(float height) {
        ((CylinderTransformer) this.transformer).setHeight(height);
    }

    public float getHeight(boolean lerp) {
        return ((CylinderTransformer) this.transformer).getHeight(lerp);
    }

    @Override
    public void setRadius(float radius) {
        ((CylinderTransformer) this.transformer).setRadius(radius);
    }

    @Override
    public void setSegments(int segments) {
        ((CylinderTransformer) this.transformer).setSegment(segments);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((CylinderTransformer) this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((CylinderTransformer) this.transformer).getSegment(lerp);
    }

    public void setAxis(CircleAxis axis) {
        this.axis = axis;
    }

    public static class CylinderTransformer extends DefaultTransformer {
        private final CircleModelInfo circleModelInfo;
        private final FloatTransformer heightTransformer;

        public CylinderTransformer(Shape managedShape, int segments, float radius, float height, Vec3 center) {
            super(managedShape, center);
            this.circleModelInfo = new CircleModelInfo(segments, radius);
            this.heightTransformer = new FloatTransformer(height);
        }

        public void setSegment(int segments) { circleModelInfo.setSegment(segments); }
        public void setRadius(float radius)   { circleModelInfo.setRadius(radius); }
        public void setHeight(float height)   { heightTransformer.setValue(height); }
        public float getHeight(boolean lerp)  { return heightTransformer.getValue(lerp); }
        public int getSegment(boolean lerp)   { return circleModelInfo.getSegment(lerp); }
        public int getSegments(boolean lerp)  { return getSegment(lerp); }
        public float getRadius(boolean lerp)  { return circleModelInfo.getRadius(lerp); }

        @Override
        public void updateTickDelta(float delta) {
            circleModelInfo.update(delta);
            heightTransformer.update(delta);
            super.updateTickDelta(delta);
        }

        @Override
        public void syncLastToTarget() {
            circleModelInfo.syncLastToTarget();
            heightTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }

        public boolean asyncModelInfo() {
            return circleModelInfo.async() || heightTransformer.async();
        }
    }
}
