package mypals.ml.builders.shapeBuilders;

import mypals.ml.shape.Shape;
import mypals.ml.shape.round.FaceCircleShape;
import mypals.ml.shape.round.SphereShape;

import java.util.function.BiConsumer;

public class SphereBuilder extends BaseBuilder<SphereBuilder, FaceCircleShape.FaceCircleTransformer> {
    private int segments = 32;
    private float radius = 1.0f;

    public SphereBuilder segments(int segments) { this.segments = segments; return this; }
    public SphereBuilder radius(float radius) { this.radius = radius; return this; }

    @Override
    public Shape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new SphereShape(type, t, center, segments, radius, color, seeThrough);
    }
}