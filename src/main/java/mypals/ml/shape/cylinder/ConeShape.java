package mypals.ml.shape.cylinder;

import mypals.ml.shape.Shape;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ConeShape extends CylinderShape {

    public ConeShape(RenderingType type, BiConsumer<CylinderTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius, float height, Color color, boolean seeThrough) {
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
    @Override
    public void generateCylinder() {
        ArrayList<Vec3> vs = new ArrayList<>();
        double halfH = height / 2.0;

        ArrayList<Vec3> baseRing = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            double x = 0, y = 0, z = 0;

            switch (axis) {
                case X -> {
                    y = radius * c;
                    z = radius * s;
                    x = -halfH;
                    baseRing.add(new Vec3(x, y, z));
                }
                case Y -> {
                    x = radius * c;
                    z = radius * s;
                    y = -halfH;
                    baseRing.add(new Vec3(x, y, z));
                }
                case Z -> {
                    x = radius * c;
                    y = radius * s;
                    z = -halfH;
                    baseRing.add(new Vec3(x, y, z));
                }
                default -> throw new IllegalStateException("Unexpected axis value: " + axis);
            }
        }

        Vec3 apex = switch (axis) {
            case X -> new Vec3(halfH, 0, 0);
            case Y -> new Vec3(0, halfH, 0);
            case Z -> new Vec3(0, 0, halfH);
        };

        Vec3 baseCenter = switch (axis) {
            case X -> new Vec3(-halfH, 0, 0);
            case Y -> new Vec3(0, -halfH, 0);
            case Z -> new Vec3(0, 0, -halfH);
        };

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 b0 = baseRing.get(i);
            Vec3 b1 = baseRing.get(next);

            vs.add(b0);
            vs.add(apex);
            vs.add(b1);
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            Vec3 b0 = baseRing.get(i);
            Vec3 b1 = baseRing.get(next);

            vs.add(baseCenter);
            vs.add(b1);
            vs.add(b0);
        }

        vertexes = vs;

    }
}
