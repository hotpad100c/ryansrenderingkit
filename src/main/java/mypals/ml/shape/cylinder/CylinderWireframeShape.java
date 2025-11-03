package mypals.ml.shape.cylinder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.basics.tags.DrawableLine;
import mypals.ml.transform.FloatValueTransformer;
import mypals.ml.transform.IntValueTransformer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class CylinderWireframeShape extends CylinderShape implements DrawableLine,LineLikeShape {
    public float lineWidth = 1.0f;
    public CylinderWireframeShape(RenderingType type, BiConsumer<CylinderWireframeTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int radius, float height, float lineWidth, Color color) {
        this(type, transform,circleAxis,center,180, radius,height,lineWidth, color, false);
    }
    public CylinderWireframeShape(RenderingType type, BiConsumer<CylinderWireframeTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius, float height, float lineWidth, Color color) {
        this(type, transform,circleAxis,center,segments, radius, height,lineWidth,color, false);
    }

    public CylinderWireframeShape(RenderingType type, BiConsumer<CylinderWireframeTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius, float height, float lineWidth, Color color, boolean seeThrough) {
        super(type, seeThrough);
        this.transformer = new CylinderWireframeTransformer(this,segments,radius,height,lineWidth);
        this.transformFunction = (defaultTransformer,shape)->transform.accept((CylinderWireframeTransformer) this.transformer, shape);
        this.segments = segments;
        this.radius = radius;
        this.height = height;
        this.lineWidth = lineWidth;
        this.color = color;
        this.centerPoint = center;
        this.transformer.setShapeCenterPos(center);
        this.setAxis(circleAxis);
        syncLastToTarget();
    }
    @Override
    public void draw(VertexBuilder builder) {
        RenderSystem.lineWidth(lineWidth);
        builder.putColor(this.color);
        for (int i = 0; i < vertexes.size(); i += 2) {
            Vec3 a = vertexes.get(i);
            Vec3 b = vertexes.get(i + 1);
            addLineSegment(builder, a.add(getShapeCenterPos()), b.add(getShapeCenterPos()));
        }
    }
    @Override
    public void generateCylinder() {
        ArrayList<Vec3> vs = new ArrayList<>();
        double halfH = height / 2.0;

        ArrayList<Vec3> baseRing = getBase(halfH);

        ArrayList<Vec3> topRing = getTop(baseRing, halfH);

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            vs.add(baseRing.get(i));
            vs.add(baseRing.get(next));
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            vs.add(topRing.get(i));
            vs.add(topRing.get(next));
        }

        for (int i = 0; i < segments; i++) {
            vs.add(baseRing.get(i));
            vs.add(topRing.get(i));
        }

        vertexes = vs;
    }

    private @NotNull ArrayList<Vec3> getTop(ArrayList<Vec3> baseRing, double halfH) {
        ArrayList<Vec3> topRing = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            Vec3 bottom = baseRing.get(i);
            Vec3 top = switch (axis) {
                case X -> new Vec3(halfH, bottom.y, bottom.z);
                case Y -> new Vec3(bottom.x, halfH, bottom.z);
                case Z -> new Vec3(bottom.x, bottom.y, halfH);
            };
            topRing.add(top);
        }
        return topRing;
    }

    private @NotNull ArrayList<Vec3> getBase(double halfH) {
        ArrayList<Vec3> baseRing = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            Vec3 point = switch (axis) {
                case X -> new Vec3(-halfH, radius * c, radius * s);
                case Y -> new Vec3(radius * c, -halfH, radius * s);
                case Z -> new Vec3(radius * c, radius * s, -halfH);
            };
            baseRing.add(point);
        }
        return baseRing;
    }

    @Override
    public void setLineWidth(float width) {
        lineWidth = width;
    }

    public static class CylinderWireframeTransformer extends LineLikeShape.DefaultLineTransformer {
        public IntValueTransformer segmentTransformer = new IntValueTransformer();
        public FloatValueTransformer radiusTransformer = new FloatValueTransformer();
        public FloatValueTransformer heightTransformer = new FloatValueTransformer();
        public CylinderWireframeTransformer(Shape managerShape,int seg,float rad,float height,float lineWidth) {
            super(managerShape);
            setSegment(seg);
            setRadius(rad);
            setHeight(height);
            setWidth(lineWidth);
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
}
