package ml.mypals.ryansrenderingkit.builders.shapeBuilders;

import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.round.FaceCircleShape;
import ml.mypals.ryansrenderingkit.shape.round.SphereShape;

public class SphereBuilder extends BaseBuilder<SphereBuilder, FaceCircleShape.FaceCircleTransformer> {
    private int segments = 32;
    private float radius = 1.0f;

    public SphereBuilder segments(int segments) {
        this.segments = segments;
        return this;
    }

    public SphereBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public SphereShape build(Shape.RenderingType type) {
        @SuppressWarnings("unchecked")
        var t = getTransformer();
        return new SphereShape(type, t, center, segments, radius, color, seeThrough);
    }
}