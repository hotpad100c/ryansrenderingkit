package mypals.ml.shape.round;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.line.StripLineShape;
import mypals.ml.transform.FloatValueTransformer;
import mypals.ml.transform.IntValueTransformer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class LineCircleShape extends StripLineShape implements CircleLikeShape {

    public int segments = 180;
    public float radius = 1;
    public CircleAxis axis = CircleAxis.X;

    public LineCircleShape(RenderingType type, BiConsumer<LineCircleTransformer, Shape> transform, CircleAxis circleAxis, Vec3d center, int radius, float lineWidth, Color color) {
        this(type, transform,circleAxis,center,180, radius,lineWidth, color, false);
    }
    public LineCircleShape(RenderingType type, BiConsumer<LineCircleTransformer, Shape> transform, CircleAxis circleAxis, Vec3d center, int segments, float radius, float lineWidth, Color color) {
        this(type, transform,circleAxis,center,segments, radius,lineWidth, color, false);
    }

    public LineCircleShape(RenderingType type, BiConsumer<LineCircleTransformer, Shape> transform, CircleAxis circleAxis, Vec3d center, int segments, float radius, float lineWidth, Color color, boolean seeThrough) {
        super(type,color,lineWidth, seeThrough);
        this.transformer = new LineCircleTransformer(this,segments,radius);
        this.transformFunction = (defaultTransformer,shape)->transform.accept((LineCircleTransformer) this.transformer, shape);
        this.segments = segments;
        this.radius = radius;
        ((LineCircleTransformer)this.transformer).setWidth(this.lineWidth);
        ((LineCircleTransformer)this.transformer).setRadius(this.radius);
        ((LineCircleTransformer)this.transformer).setSegment(this.segments);
        this.setAxis(circleAxis);
        this.transformer.setShapeCenterPos(this.calculateShapeCenterPos());

        syncLastToTarget();
    }

    public void generateCircle(){
        ArrayList<Vec3d> vs = new ArrayList<>();
        Vec3d center = getShapeCenterPos();
        for (int i = 0; i < segments; i++) {
            double theta = (2 * Math.PI * i) / segments;
            double x, y, z;

            switch (axis) {
                case X -> {
                    y = center.y + radius * Math.cos(theta);
                    z = center.z + radius * Math.sin(theta);
                    x = center.x;
                }
                case Y -> {
                    x = center.x + radius * Math.cos(theta);
                    z = center.z + radius * Math.sin(theta);
                    y = center.y;
                }
                case Z -> {
                    x = center.x + radius * Math.cos(theta);
                    y = center.y + radius * Math.sin(theta);
                    z = center.z;
                }
                default -> throw new IllegalStateException("Unexpected axis value: " + axis);
            }

            vs.add(new Vec3d(x, y, z));
        }
        if (!vs.isEmpty()) {
            vs.add(vs.getFirst());
        }
        this.setVertexes(vs);
    }
    public void setAxis(CircleAxis circleAxis){
        this.axis = circleAxis;
        generateCircle();
    }
    public static class LineCircleTransformer extends LineLikeShape.DefaultLineTransformer {
        public IntValueTransformer segmentTransformer = new IntValueTransformer();
        public FloatValueTransformer radiusTransformer = new FloatValueTransformer();
        public LineCircleTransformer(Shape managerShape) {
            super(managerShape);
        }
        public LineCircleTransformer(Shape managerShape,int seg,float rad) {
            super(managerShape);
            setSegment(seg);
            setRadius(rad);
        }
        public void setSegment(int segment){
            this.segmentTransformer.setTargetValue(segment);
        }
        public void setRadius(float radius){
            this.radiusTransformer.setTargetValue(radius);
        }
        @Override
        public void applyTransformations(MatrixStack matrixStack){
            super.applyTransformations(matrixStack);
            float deltaTime = getTickDelta();
            if(this.managedShape instanceof LineCircleShape circleShape) {
                this.segmentTransformer.updateValue(circleShape::setSegments,deltaTime);
                this.radiusTransformer.updateValue(circleShape::setRadius,deltaTime);
            }
        }
        @Override
        public void syncLastToTarget(){
            this.segmentTransformer.syncLastToTarget();
            this.radiusTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }
    }
    @Override
    public void setRadius(float radius) {
        boolean rebuild = this.radius != radius;
        this.radius = radius;
        if(rebuild){
            generateCircle();
        }

    }

    @Override
    public void setSegments(int segments) {
        boolean rebuild = this.segments != segments;
        this.segments = segments;
        if(rebuild){
            generateCircle();
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
}
