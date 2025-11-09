package mypals.ml.shape.cylinder;

import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConeShape extends CylinderShape {

    public ConeShape(RenderingType type, Consumer<CylinderTransformer> transform, CircleAxis circleAxis, Vec3 center, int segments, float radius, float height, Color color, boolean seeThrough) {
        super(type, transform, circleAxis, center, segments, radius, height, color, seeThrough);
    }
    private List<Vec3> generateConeVertices(double halfH, int segments, float radius, CircleAxis axis) {
        List<Vec3> vertices = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            double theta = 2 * Math.PI * i / segments;
            double c = Math.cos(theta);
            double s = Math.sin(theta);

            Vec3 point = switch (axis) {
                case X -> new Vec3(-halfH, radius * c, radius * s);
                case Y -> new Vec3(radius * c, -halfH, radius * s);
                case Z -> new Vec3(radius * c, radius * s, -halfH);
            };
            vertices.add(point);
        }

        Vec3 apex = switch (axis) {
            case X -> new Vec3(halfH, 0, 0);
            case Y -> new Vec3(0, halfH, 0);
            case Z -> new Vec3(0, 0, halfH);
        };
        vertices.add(apex);

        Vec3 baseCenter = switch (axis) {
            case X -> new Vec3(-halfH, 0, 0);
            case Y -> new Vec3(0, -halfH, 0);
            case Z -> new Vec3(0, 0, -halfH);
        };
        vertices.add(baseCenter);

        return vertices;
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();
        List<Integer> indices = new ArrayList<>();

        float height = ((CylinderTransformer)this.transformer).getHeight(lerp);
        int segments = ((CylinderTransformer)this.transformer).getSegments(lerp);
        float radius = ((CylinderTransformer)this.transformer).getRadius(lerp);
        double halfH = height / 2.0;

        List<Vec3> verts = generateConeVertices(halfH, segments, radius, axis);
        model_vertexes.addAll(verts);

        int apexIndex = segments;
        int baseCenterIndex = segments + 1;

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(i);
            indices.add(apexIndex);
            indices.add(next);
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            indices.add(baseCenterIndex);
            indices.add(next);
            indices.add(i);
        }

        this.indexBuffer = indices.stream().mapToInt(Integer::intValue).toArray();
    }

}
