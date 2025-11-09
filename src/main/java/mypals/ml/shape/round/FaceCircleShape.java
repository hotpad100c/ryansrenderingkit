package mypals.ml.shape.round;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.CircleModelInfo;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.Consumer;

public class FaceCircleShape extends Shape implements CircleLikeShape {

    public CircleAxis axis = CircleAxis.X;

    public FaceCircleShape(RenderingType type,
                           Consumer<FaceCircleTransformer> transform,
                           CircleAxis axis,
                           Vec3 center,
                           int segments,
                           float radius,
                           Color color,
                           boolean seeThrough) {
        super(type, color, seeThrough);
        this.axis = axis;

        this.transformer = new FaceCircleTransformer(this, segments, radius,center);
        this.transformFunction = (t) -> transform.accept((FaceCircleTransformer) this.transformer);

        generateRawGeometry(true);
        syncLastToTarget();
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();
        if (this.getSegments(lerp) < 3) return;

        int segments = getSegments(lerp);
        float radius = getRadius(lerp);

        model_vertexes.add(Vec3.ZERO);

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
            model_vertexes.add(new Vec3(x, y, z));
        }

        indexBuffer = new int[segments * 3];
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indexBuffer[i * 3] = 0;      // center
            indexBuffer[i * 3 + 1] = i + 1;
            indexBuffer[i * 3 + 2] = next + 1;
        }
    }

    @Override
    public void setRadius(float radius) {
        ((FaceCircleTransformer)this.transformer).setRadius(radius);
    }

    @Override
    public void setSegments(int segments) {
        ((FaceCircleTransformer)this.transformer).setSegment(segments);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((FaceCircleTransformer)this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((FaceCircleTransformer)this.transformer).getSegment(lerp);
    }

    public static class FaceCircleTransformer extends DefaultTransformer {
        public CircleModelInfo circleModelInfo;
        public FaceCircleTransformer(Shape managedShape, int seg, float rad,Vec3 center) {
            super(managedShape,center);
            circleModelInfo = new CircleModelInfo(seg,rad);
        }

        public void setSegment(int segment) { this.circleModelInfo.setSegment(segment); }
        public void setRadius(float radius) { this.circleModelInfo.setRadius(radius); }
        public int getSegment(boolean lerp) {return this.circleModelInfo.getSegment(lerp); }
        public float getRadius(boolean lerp) {return this.circleModelInfo.getRadius(lerp); }

        @Override
        public void updateTickDelta(float delta) {
            this.circleModelInfo.update(delta);
            super.updateTickDelta(delta);
        }
        @Override
        public void syncLastToTarget() {
            this.circleModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }
        public boolean asyncModelInfo(){
            return circleModelInfo.async();
        }
    }
}
