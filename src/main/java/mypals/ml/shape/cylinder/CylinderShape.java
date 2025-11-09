package mypals.ml.shape.cylinder;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.tags.DrawableTriangle;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.CircleModelInfo;
import mypals.ml.transform.valueTransformers.FloatTransformer;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CylinderShape extends Shape implements CircleLikeShape, DrawableTriangle {

    public CircleAxis axis = CircleAxis.X;
    public Color color = Color.WHITE;

    public CylinderShape(RenderingType type, Consumer<CylinderTransformer> transform,
                         CircleAxis circleAxis, Vec3 center, int segments,
                         float radius, float height, Color color, boolean seeThrough) {
        super(type, shape -> {}, color, center, seeThrough);

        this.transformer = new CylinderTransformer(this, segments, radius, height,center);
        this.transformFunction = t -> transform.accept((CylinderTransformer) this.transformer);
        this.axis = circleAxis;
        generateRawGeometry(false);
        syncLastToTarget();
    }

    public CylinderShape(RenderingType type, boolean seeThrough) {
        super(type, Color.WHITE, seeThrough);
    }

    void generateCylinderVertices(boolean lerp) {
        model_vertexes.clear();

        float height = getHeight(lerp);
        float radius = getRadius(lerp);
        int segments = getSegments(lerp);
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
            model_vertexes.add(bottom);
        }

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            Vec3 top = switch (axis) {
                case X -> new Vec3( halfH, radius * c, radius * s);
                case Y -> new Vec3(radius * c,  halfH, radius * s);
                case Z -> new Vec3(radius * c, radius * s,  halfH);
            };
            model_vertexes.add(top);
        }

        model_vertexes.add(switch (axis) {
            case X -> new Vec3(-halfH, 0, 0);
            case Y -> new Vec3(0, -halfH, 0);
            case Z -> new Vec3(0, 0, -halfH);
        });

        model_vertexes.add(switch (axis) {
            case X -> new Vec3( halfH, 0, 0);
            case Y -> new Vec3(0,  halfH, 0);
            case Z -> new Vec3(0, 0,  halfH);
        });
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        generateCylinderVertices(lerp);

        List<Integer> indices = new ArrayList<>();
        int segments = getSegments(lerp);

        int bottomStart = 0;
        int topStart = segments;
        int bottomCenter = 2 * segments;
        int topCenter = 2 * segments + 1;

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            int b0 = bottomStart + i;
            int b1 = bottomStart + next;
            int t0 = topStart + i;
            int t1 = topStart + next;

            indices.add(b0); indices.add(t0); indices.add(t1);
            indices.add(b0); indices.add(t1); indices.add(b1);
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(bottomCenter);
            indices.add(bottomStart + next);
            indices.add(bottomStart + i);
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(topCenter);
            indices.add(topStart + i);
            indices.add(topStart + next);
        }

        this.indexBuffer = indices.stream().mapToInt(Integer::intValue).toArray();
    }

    public void setAxis(CircleAxis circleAxis) {
        this.axis = circleAxis;
        generateRawGeometry(true);
    }

    public static class CylinderTransformer extends DefaultTransformer {

        public CircleModelInfo circleModelInfo;
        public FloatTransformer heightTransformer;

        public CylinderTransformer(Shape managedShape, int seg, float rad, float height,Vec3 vec3) {
            super(managedShape,vec3);
            circleModelInfo = new CircleModelInfo(seg,rad);
            heightTransformer = new FloatTransformer(height);
        }

        public void setSegment(int segment) { this.circleModelInfo.setSegment(segment); }
        public void setRadius(float radius) { this.circleModelInfo.setRadius(radius); }
        public void setHeight(float height) { this.heightTransformer.setTargetValue(height); }

        public float getRadius(boolean lerp) {
            return circleModelInfo.getRadius(lerp);
        }

        public int getSegments(boolean lerp) {
            return circleModelInfo.getSegment(lerp);
        }
        public float getHeight(boolean lerp) { return heightTransformer.getValue(lerp); }
        @Override
        public void syncLastToTarget() {
            this.circleModelInfo.syncLastToTarget();
            this.heightTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }
        @Override
        public boolean asyncModelInfo(){
            return circleModelInfo.async() || heightTransformer.async();
        }
    }

    @Override
    public void setRadius(float radius) {
        ((CylinderTransformer)this.transformer).setRadius(radius);
    }

    public void setHeight(float height) {
        ((CylinderTransformer)this.transformer).setHeight(height);
    }

    @Override
    public void setSegments(int segments) {
        ((CylinderTransformer)this.transformer).setSegment(segments);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((CylinderTransformer)this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((CylinderTransformer)this.transformer).getSegments(lerp);
    }

    public float getHeight(boolean lerp) { return ((CylinderTransformer)this.transformer).getHeight(lerp); }
}

