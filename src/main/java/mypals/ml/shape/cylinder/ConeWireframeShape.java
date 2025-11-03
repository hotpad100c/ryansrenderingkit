package mypals.ml.shape.cylinder;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.tags.DrawableLine;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class ConeWireframeShape extends CylinderWireframeShape {
    public ConeWireframeShape(RenderingType type, BiConsumer<CylinderWireframeShape.CylinderWireframeTransformer, Shape> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius, float height, float lineWidth, Color color, boolean seeThrough) {
        super(type, transform,circleAxis,center,segments, radius, height,lineWidth,color, seeThrough);
    }
    @Override
    public void generateCylinder() {
        ArrayList<Vec3> vs = new ArrayList<>();
        double halfH = height / 2.0;

        ArrayList<Vec3> baseRing = getBase(halfH);

        Vec3 apex = switch (axis) {
            case X -> new Vec3( halfH, 0, 0);
            case Y -> new Vec3(0,  halfH, 0);
            case Z -> new Vec3(0, 0,  halfH);
            default -> throw new IllegalStateException("Unexpected axis value: " + axis);
        };

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            vs.add(baseRing.get(i));
            vs.add(baseRing.get(next));
        }

        for (int i = 0; i < segments; i++) {
            vs.add(baseRing.get(i));
            vs.add(apex);
        }

        vertexes = vs;
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
                default -> throw new IllegalStateException("Unexpected axis value: " + axis);
            };
            baseRing.add(point);
        }
        return baseRing;
    }
}
