package mypals.ml.shape.round;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.CircleLikeShape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.transform.shapeTransformers.DefaultTransformer;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.CircleModelInfo;
import mypals.ml.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.function.Consumer;

public class LineCircleShape extends Shape implements CircleLikeShape, LineLikeShape {

    public CircleAxis axis = CircleAxis.X;

    public LineCircleShape(RenderingType type,
                           Consumer<LineCircleTransformer> transform,
                           CircleAxis axis,
                           Vec3 center,
                           int segments,
                           float radius,
                           float width,
                           Color color,
                           boolean seeThrough) {
        super(type, color, seeThrough);
        this.axis = axis;

        this.transformer = new LineCircleTransformer(this, segments, radius,width, center);
        this.transformFunction = t -> transform.accept((LineCircleTransformer) this.transformer);

        generateRawGeometry(true);
        syncLastToTarget();
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        model_vertexes.clear();
        int segments = getSegments(lerp);
        float radius = getRadius(lerp);
        if (segments < 3) return;

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

        indexBuffer = new int[segments * 2];
        for (int i = 0; i < segments; i++) {
            indexBuffer[i * 2] = i;
            indexBuffer[i * 2 + 1] = (i + 1) % segments;
        }
    }

    @Override
    public void setRadius(float radius) {
        ((LineCircleTransformer) this.transformer).setRadius(radius);
    }

    @Override
    public void setSegments(int segments) {
        ((LineCircleTransformer) this.transformer).setSegment(segments);
    }

    @Override
    public float getRadius(boolean lerp) {
        return ((LineCircleTransformer) this.transformer).getRadius(lerp);
    }

    @Override
    public int getSegments(boolean lerp) {
        return ((LineCircleTransformer) this.transformer).getSegment(lerp);
    }

    @Override
    public void setLineWidth(float width) {
        ((LineCircleTransformer) this.transformer).setWidth(width);
    }

    @Override
    public float getLineWidth(boolean lerp) {
        return ((LineCircleTransformer) this.transformer).getWidth(lerp);
    }

    public static class LineCircleTransformer extends DefaultTransformer {
        private final CircleModelInfo circleModelInfo;
        private final LineModelInfo lineModelInfo;
        public LineCircleTransformer(Shape managedShape, int segments, float radius,float width, Vec3 center) {
            super(managedShape, center);
            circleModelInfo = new CircleModelInfo(segments, radius);
            lineModelInfo = new LineModelInfo(width);
        }

        public void setSegment(int segments) {
            circleModelInfo.setSegment(segments);
        }

        public void setRadius(float radius) {
            circleModelInfo.setRadius(radius);
        }
        public void setWidth(float radius) {
            lineModelInfo.setWidth(radius);
        }

        public float getWidth(boolean lerp) {
            return lineModelInfo.getWidth(lerp);
        }
        public int getSegment(boolean lerp) {
            return circleModelInfo.getSegment(lerp);
        }

        public float getRadius(boolean lerp) {
            return circleModelInfo.getRadius(lerp);
        }

        @Override
        public void updateTickDelta(float delta) {
            circleModelInfo.update(delta);
            lineModelInfo.update(delta);
            super.updateTickDelta(delta);
        }

        @Override
        public void syncLastToTarget() {
            circleModelInfo.syncLastToTarget();
            lineModelInfo.syncLastToTarget();
            super.syncLastToTarget();
        }
        public boolean asyncModelInfo(){
            return circleModelInfo.async() || lineModelInfo.async();
        }
    }
    @Override
    protected void drawInternal(VertexBuilder builder) {
        RenderSystem.lineWidth(getLineWidth(true));

        int n = model_vertexes.size();
        if (n < 2) return;

        Vec3 first = model_vertexes.getFirst();
        builder.putColor(new Color(0, 0, 0, 0));
        builder.putVertex(first, Vec3.ZERO);
        builder.putColor(baseColor);
        for (int i = 0; i < n; i++) {

            Vec3 normal;
            if (i == 0) {
                Vec3 dir = model_vertexes.get(1).subtract(model_vertexes.get(0));
                normal = dir.normalize();
            } else if (i == n - 1) {
                Vec3 dir = model_vertexes.get(n - 1).subtract(model_vertexes.get(n - 2));
                normal = dir.normalize();
            } else {
                Vec3 prevDir = model_vertexes.get(i).subtract(model_vertexes.get(i - 1));
                Vec3 nextDir = model_vertexes.get(i + 1).subtract(model_vertexes.get(i));
                normal = prevDir.add(nextDir).normalize();
                if (Double.isNaN(normal.x) || Double.isNaN(normal.y) || Double.isNaN(normal.z)) {
                    Vec3 fallback = nextDir.lengthSqr() > 0 ? nextDir : prevDir;
                    normal = fallback.normalize();
                }
            }

            Vec3 pos = model_vertexes.get(i);
            builder.putVertex(pos,normal);
        }
        Vec3 finish = model_vertexes.getFirst();

        builder.putVertex(finish,Vec3.ZERO);

        Vec3 last = model_vertexes.get(n - 1);
        builder.putColor(new Color(0, 0, 0, 0));
        builder.putVertex(last,Vec3.ZERO);
    }
}

