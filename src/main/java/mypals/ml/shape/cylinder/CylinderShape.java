package mypals.ml.shape.cylinder;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.tags.DrawableTriangle;
import mypals.ml.transform.FloatValueTransformer;
import mypals.ml.transform.IntValueTransformer;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class CylinderShape extends Shape implements CircleLikeShape, DrawableTriangle {

    public int segments = 180;
    public float radius = 1;
    public float height = 1;
    public CircleAxis axis = CircleAxis.X;
    public ArrayList<Vec3> vertexes = new ArrayList<>();
    public Color color = Color.white;

    public CylinderShape(RenderingType type, BiConsumer<CylinderTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int radius,float height, Color color) {
        this(type, transform,circleAxis,center,180, radius,height, color, false);
    }
    public CylinderShape(RenderingType type, BiConsumer<CylinderTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius,float height, Color color) {
        this(type, transform,circleAxis,center,segments, radius, height,color, false);
    }

    public CylinderShape(RenderingType type, BiConsumer<CylinderTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius,float height, Color color, boolean seeThrough) {
        super(type, seeThrough);
        this.transformer = new CylinderTransformer(this,segments,radius,height);
        this.transformFunction = (defaultTransformer,shape)->transform.accept((CylinderTransformer) this.transformer, shape);
        this.segments = segments;
        this.radius = radius;
        this.height = height;
        this.color = color;
        this.centerPoint = center;
        this.transformer.setShapeCenterPos(center);
        this.setAxis(circleAxis);
        syncLastToTarget();
    }

    public CylinderShape(RenderingType type, boolean seeThrough) {
        super(type,seeThrough);
    }


    public void generateCylinder() {
        ArrayList<Vec3> vs = new ArrayList<>();
        double halfH = height / 2.0;

        ArrayList<Vec3> bottomRing = new ArrayList<>();
        ArrayList<Vec3> topRing = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            double theta = (2 * Math.PI * i) / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            double x, y, z;

            switch (axis) {
                case X -> {
                    y = radius * c;
                    z = radius * s;
                    x = 0;
                    bottomRing.add(new Vec3(x - halfH, y, z));
                    topRing.add(new Vec3(x + halfH, y, z));
                }
                case Y -> {
                    x = radius * c;
                    z = radius * s;
                    y = 0;
                    bottomRing.add(new Vec3(x, y - halfH, z));
                    topRing.add(new Vec3(x, y + halfH, z));
                }
                case Z -> {
                    x = radius * c;
                    y = radius * s;
                    z = 0;
                    bottomRing.add(new Vec3(x, y, z - halfH));
                    topRing.add(new Vec3(x, y, z + halfH));
                }
            }
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;

            Vec3 b0 = bottomRing.get(i);
            Vec3 b1 = bottomRing.get(next);
            Vec3 t0 = topRing.get(i);
            Vec3 t1 = topRing.get(next);

            vs.add(b0);
            vs.add(t0);
            vs.add(t1);

            vs.add(b0);
            vs.add(t1);
            vs.add(b1);
        }

        Vec3 baseCenter = switch (axis) {
            case X -> new Vec3(-halfH, 0, 0);
            case Y -> new Vec3(0, -halfH, 0);
            case Z -> new Vec3(0, 0, -halfH);
        };

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 b0 = bottomRing.get(i);
            Vec3 b1 = bottomRing.get(next);

            vs.add(baseCenter);
            vs.add(b1);
            vs.add(b0);
        }

        Vec3 topCenter = switch (axis) {
            case X -> new Vec3(halfH, 0, 0);
            case Y -> new Vec3(0, halfH, 0);
            case Z -> new Vec3(0, 0, halfH);
        };

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 t0 = topRing.get(i);
            Vec3 t1 = topRing.get(next);

            vs.add(topCenter);
            vs.add(t0);
            vs.add(t1);
        }

        vertexes = vs;
    }

    public void setAxis(CircleAxis circleAxis){
        this.axis = circleAxis;
        generateCylinder();
    }
    @Override
    public void draw(VertexBuilder builder) {

        builder.putColor(this.color);
        for(Vec3 v : vertexes){
            builder.putVertex(v.add(getShapeCenterPos()));
        }

    }
    public static class CylinderTransformer extends DefaultTransformer {
        public IntValueTransformer segmentTransformer = new IntValueTransformer();
        public FloatValueTransformer radiusTransformer = new FloatValueTransformer();
        public FloatValueTransformer heightTransformer = new FloatValueTransformer();
        public CylinderTransformer(Shape managerShape,int seg,float rad,float height) {
            super(managerShape);
            setSegment(seg);
            setRadius(rad);
            setHeight(height);
        }
        public void setSegment(int segment){
            this.segmentTransformer.setTargetValue(segment);
        }
        public void setRadius(float radius){
            this.radiusTransformer.setTargetValue(radius);
        }
        public void setHeight(float height){
            this.heightTransformer.setTargetValue(height);
        }
        @Override
        public void applyTransformations(PoseStack matrixStack){
            super.applyTransformations(matrixStack);
            float deltaTime = getTickDelta();
            if(this.managedShape instanceof CylinderShape cylinderShape) {
                this.segmentTransformer.updateValue(cylinderShape::setSegments,deltaTime);
                this.radiusTransformer.updateValue(cylinderShape::setRadius,deltaTime);
                this.heightTransformer.updateValue(cylinderShape::setHeight,deltaTime);
            }
        }
        @Override
        public void syncLastToTarget(){
            this.segmentTransformer.syncLastToTarget();
            this.radiusTransformer.syncLastToTarget();
            this.heightTransformer.syncLastToTarget();
            super.syncLastToTarget();
        }
    }
    @Override
    public void setRadius(float radius) {
        boolean rebuild = this.radius != radius;
        this.radius = radius;
        if(rebuild){
            generateCylinder();
        }

    }

    public void setHeight(float height) {
        boolean rebuild = this.height != height;
        this.height = height;
        if(rebuild){
            generateCylinder();
        }

    }

    @Override
    public void setSegments(int segments) {
        boolean rebuild = this.segments != segments;
        this.segments = segments;
        if(rebuild){
            generateCylinder();
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
    public float getHeight(){
        return height;
    }
}
