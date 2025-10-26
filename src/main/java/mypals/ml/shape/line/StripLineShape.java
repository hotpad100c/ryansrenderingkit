package mypals.ml.shape.line;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.ShapeBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.basics.core.StripLineLikeShape;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class StripLineShape extends Shape implements StripLineLikeShape {
    public List<Vec3d> vertexes = new ArrayList<>();
    public float lineWidth;
    public Color color;

    public StripLineShape(RenderingType type, BiConsumer<? super LineLikeShape.DefaultLineTransformer, Shape> transform, List<Vec3d> vertexes, float lineWidth, Color color) {
        this(type,transform,vertexes,lineWidth,color,false);

    }

    public StripLineShape(RenderingType type) {
        super(type);
    }

    public StripLineShape(RenderingType type, boolean seeThrough) {
        super(type,seeThrough);
    }
    public StripLineShape(RenderingType type, Color color,float lineWidth,boolean seeThrough) {
        super(type,seeThrough);
        this.color = color;
        this.lineWidth = lineWidth;

    }
    public StripLineShape(RenderingType type, BiConsumer<? super LineLikeShape.DefaultLineTransformer, Shape> transform, List<Vec3d> vertexes, float lineWidth, Color color, boolean seeThrough) {
        super(type, seeThrough);
        this.setVertexes(vertexes);
        this.color = color;
        this.lineWidth = lineWidth;
        this.transformer = new LineLikeShape.DefaultLineTransformer(this);
        this.transformer.setShapeCenterPos(this.calculateShapeCenterPos());
        this.transformFunction = (defaultTransformer,shape)->transform.accept((LineLikeShape.DefaultLineTransformer) this.transformer, shape);
        ((LineLikeShape.DefaultLineTransformer)this.transformer).setWidth(this.lineWidth);
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

    @Override
    public void setShapeCenterPos(Vec3d newCenter) {
        super.setShapeCenterPos(newCenter);

        if (vertexes.isEmpty()) {
            return;
        }

        Vec3d currentCenter = calculateShapeCenterPos();

        Vec3d offset = newCenter.subtract(currentCenter);

        for (int i = 0; i < vertexes.size(); i++) {
            vertexes.set(i, vertexes.get(i).add(offset));
        }
        //syncLastToTarget();
    }

    @Override
    public void setVertexes(List<Vec3d> vertexes) {
        this.vertexes = vertexes;
    }

    @Override
    public List<Vec3d> getVertexes() {
        return this.vertexes;
    }
    @Override
    public void draw(ShapeBuilder builder) {
        RenderSystem.lineWidth(lineWidth);
        builder.putColor(color);
        int n = vertexes.size();
        if (n < 2) return;

        for (int i = 0; i < n; i++) {
            Vec3d normal;

            if (i == 0) {
                Vec3d dir = vertexes.get(i + 1).subtract(vertexes.get(i));
                normal = dir.normalize();
            } else if (i == n - 1) {
                Vec3d dir = vertexes.get(i).subtract(vertexes.get(i - 1));
                normal = dir.normalize();
            } else {
                Vec3d prevDir = vertexes.get(i).subtract(vertexes.get(i - 1));
                Vec3d nextDir = vertexes.get(i + 1).subtract(vertexes.get(i));
                normal = prevDir.add(nextDir).normalize();
            }

            Vec3d pos = vertexes.get(i);
            builder.putVertex((float) pos.x, (float) pos.y, (float) pos.z,
                    (float) normal.x, (float) normal.y, (float) normal.z);
        }
    }

    @Override
    public void setLineWidth(float width) {
        this.lineWidth = width;
    }
}
