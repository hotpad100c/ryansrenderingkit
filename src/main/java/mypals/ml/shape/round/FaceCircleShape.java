package mypals.ml.shape.round;

import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.tags.DrawableTriangle;
import mypals.ml.transform.FloatValueTransformer;
import mypals.ml.transform.IntValueTransformer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class FaceCircleShape extends Shape implements CircleLikeShape, DrawableTriangle {

    public int segments = 180;
    public float radius = 1;
    public CircleAxis axis = CircleAxis.X;
    public ArrayList<Vec3d> vertexes = new ArrayList<>();
    public Color color = Color.white;

    public FaceCircleShape(RenderingType type, BiConsumer<FaceCircleTransformer, Shape> transform, CircleAxis circleAxis, Vec3d center, int radius, Color color) {
        this(type, transform,circleAxis,center,180, radius, color, false);
    }
    public FaceCircleShape(RenderingType type, BiConsumer<FaceCircleTransformer, Shape> transform, CircleAxis circleAxis, Vec3d center, int segments, float radius, Color color) {
        this(type, transform,circleAxis,center,segments, radius, color, false);
    }

    public FaceCircleShape(RenderingType type, BiConsumer<FaceCircleTransformer, Shape> transform, CircleAxis circleAxis, Vec3d center, int segments, float radius,Color color, boolean seeThrough) {
        super(type, seeThrough);
        this.transformer = new FaceCircleTransformer(this,segments,radius);
        this.transformFunction = (defaultTransformer,shape)->transform.accept((FaceCircleTransformer) this.transformer, shape);
        this.segments = segments;
        this.radius = radius;
        this.color = color;
        ((FaceCircleTransformer)this.transformer).setRadius(this.radius);
        ((FaceCircleTransformer)this.transformer).setSegment(this.segments);
        this.setAxis(circleAxis);
        this.centerPoint = center;
        this.transformer.setShapeCenterPos(this.calculateShapeCenterPos());
        syncLastToTarget();
    }
    @Override
    public Vec3d calculateShapeCenterPos() {
        if (vertexes.isEmpty()) {
            return Vec3d.ZERO;
        }

        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3d vertex : vertexes) {
            sumX += vertex.x;
            sumY += vertex.y;
            sumZ += vertex.z;
        }

        int count = vertexes.size();
        return new Vec3d(sumX / count, sumY / count, sumZ / count);
    }
    public void generateCircle(){
        ArrayList<Vec3d> vs = new ArrayList<>();
        Vec3d center = Vec3d.ZERO;
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
        vertexes = vs;
    }
    public void setAxis(CircleAxis circleAxis){
        this.axis = circleAxis;
        generateCircle();
    }
    @Override
    public void draw(ShapeBuilder builder) {

        builder.putColor(this.color);

        builder.putVertex(this.centerPoint);
        for(Vec3d v : vertexes){
            builder.putVertex(v.add(getShapeCenterPos()));
        }

    }
    public static class FaceCircleTransformer extends DefaultTransformer {
        public IntValueTransformer segmentTransformer = new IntValueTransformer();
        public FloatValueTransformer radiusTransformer = new FloatValueTransformer();
        public FaceCircleTransformer(Shape managerShape,int seg,float rad) {
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
            if(this.managedShape instanceof FaceCircleShape circleShape) {
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
