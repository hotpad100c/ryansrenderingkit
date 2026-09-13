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

        int segments = Math.max(4, getSegments(lerp));
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

    protected List<Vector3f[]> getLocalCylinderEdges(boolean lerp) {
        generateCylinderVertices(lerp);
        int segments = getSegments(lerp);
        List<Vector3f[]> edges = new ArrayList<>();
        // Bottom ring
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 p1 = modelVertexes.get(i);
            Vec3 p2 = modelVertexes.get(next);
            edges.add(new Vector3f[]{
                    new Vector3f((float) p1.x, (float) p1.y, (float) p1.z),
                    new Vector3f((float) p2.x, (float) p2.y, (float) p2.z)
            });
        }
        // Top ring
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 p1 = modelVertexes.get(segments + i);
            Vec3 p2 = modelVertexes.get(segments + next);
            edges.add(new Vector3f[]{
                    new Vector3f((float) p1.x, (float) p1.y, (float) p1.z),
                    new Vector3f((float) p2.x, (float) p2.y, (float) p2.z)
            });
        }
        // Side connectors
        for (int i = 0; i < segments; i++) {
            Vec3 p1 = modelVertexes.get(i);
            Vec3 p2 = modelVertexes.get(segments + i);
            edges.add(new Vector3f[]{
                    new Vector3f((float) p1.x, (float) p1.y, (float) p1.z),
                    new Vector3f((float) p2.x, (float) p2.y, (float) p2.z)
            });
        }
        return edges;
    }

    protected List<Vector3f[]> getLocalWireframeEdges(boolean lerp) {
        return getLocalCylinderEdges(lerp);
    }

    protected Vector3f localPoint(float axial, float u, float v) {
        return switch (axis) {
            case X -> new Vector3f(axial, u, v);
            case Y -> new Vector3f(u, axial, v);
            case Z -> new Vector3f(u, v, axial);
        };
    }

    protected static Vector3f vector(Vec3 value) {
        return new Vector3f((float) value.x, (float) value.y, (float) value.z);
    }

    protected void addDiscTriangles(List<Transformation> panels, float axial, float radius, int segments,
                                    boolean reverse, Quaternionf rotation, Vec3 centerOffset) {
        Vector3f center = localPoint(axial, 0.0F, 0.0F);
        for (int i = 0; i < segments; i++) {
            double angleA = 2.0D * Math.PI * i / segments;
            double angleB = 2.0D * Math.PI * (i + 1) / segments;
            Vector3f a = localPoint(axial, radius * (float) Math.cos(angleA), radius * (float) Math.sin(angleA));
            Vector3f b = localPoint(axial, radius * (float) Math.cos(angleB), radius * (float) Math.sin(angleB));
            panels.addAll(reverse
                    ? DisplayTransformHelper.localTextTriangle(center, b, a, rotation, centerOffset)
                    : DisplayTransformHelper.localTextTriangle(center, a, b, rotation, centerOffset));
        }
    }

    protected List<Transformation> getLocalCylinderPanels(boolean lerp, Vec3 centerOffset) {
        generateCylinderVertices(lerp);
        int segments = Math.max(4, getSegments(lerp));
        List<Transformation> panels = new ArrayList<>();
        Quaternionf rotation = transformer.getWorldRotation();

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 b = modelVertexes.get(i);
            Vec3 bn = modelVertexes.get(next);
            Vec3 t = modelVertexes.get(segments + i);
            Vector3f bottom = new Vector3f((float) b.x, (float) b.y, (float) b.z);
            Vector3f bottomNext = new Vector3f((float) bn.x, (float) bn.y, (float) bn.z);
            Vector3f top = new Vector3f((float) t.x, (float) t.y, (float) t.z);
            Transformation panel = DisplayTransformHelper.localTextPanel(
                    bottom,
                    top.sub(bottom, new Vector3f()),
                    bottomNext.sub(bottom, new Vector3f()),
                    rotation,
                    centerOffset
            );
            if (panel != null) panels.add(panel);
        }

        float halfHeight = getHeight(lerp) * 0.5F;
        float radius = getRadius(lerp);
        addDiscTriangles(panels, -halfHeight, radius, segments, false, rotation, centerOffset);
        addDiscTriangles(panels, halfHeight, radius, segments, true, rotation, centerOffset);
        return panels;
    }

    protected List<Transformation> getLocalFacePanels(boolean lerp, Vec3 centerOffset) {
        return getLocalCylinderPanels(lerp, centerOffset);
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 center = transformer.getWorldPivot();

        if (renderFace) {
            for (Transformation panel : getLocalFacePanels(false, null)) {
                this.displays.add(VirtualDisplay.solidTextPanel(
                                level, center.x, center.y, center.z, this.baseColor.getRGB())
                        .bright()
                        .seeThrough(this.seeThrough, this.baseColor.getRGB())
                        .transform(panel));
            }
        }
        if (renderWireframe) {
            Quaternionf rotation = transformer.getWorldRotation();
            for (Vector3f[] edge : getLocalWireframeEdges(false)) {
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
        List<Transformation> panels = renderFace ? getLocalFacePanels(true, centerOffset) : List.of();
        List<Vector3f[]> edges = renderWireframe ? getLocalWireframeEdges(true) : List.of();
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
