package mypals.ml.shape.line;

import com.mojang.blaze3d.systems.RenderSystem;
import mypals.ml.builders.vertexBuilders.VertexBuilder;
import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.core.LineLikeShape;
import mypals.ml.shape.basics.core.StripLineLikeShape;
import net.minecraft.world.phys.Vec3;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class StripLineShape extends Shape implements StripLineLikeShape {
    public List<Vec3> vertexes = new ArrayList<>();
    public float lineWidth;
    public Color color;

    public List<Color> vertexColors = new ArrayList<>();
    public StripLineShape(RenderingType type, Color color,float lineWidth,boolean seeThrough) {
        super(type,seeThrough);
        this.color = color;
        this.lineWidth = lineWidth;

    }
    public StripLineShape(RenderingType type, BiConsumer<? super LineLikeShape.DefaultLineTransformer, Shape> transform, List<Vec3> vertexes, float lineWidth, Color color, boolean seeThrough) {
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
    public Vec3 calculateShapeCenterPos() {
        if (vertexes.isEmpty()) {
            return Vec3.ZERO;
        }

        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3 vertex : vertexes) {
            sumX += vertex.x;
            sumY += vertex.y;
            sumZ += vertex.z;
        }

        int count = vertexes.size();
        return new Vec3(sumX / count, sumY / count, sumZ / count);
    }

    public void setVertexColors(List<Color> colors){
        vertexColors = colors;
    }
    @Override
    public void setShapeCenterPos(Vec3 newCenter) {
        super.setShapeCenterPos(newCenter);

        if (vertexes.isEmpty()) {
            return;
        }

        Vec3 currentCenter = calculateShapeCenterPos();

        Vec3 offset = newCenter.subtract(currentCenter);

        vertexes.replaceAll(vec3 -> vec3.add(offset));
        //syncLastToTarget();
    }

    @Override
    public void setVertexes(List<Vec3> vertexes) {
        this.vertexes = vertexes;
    }

    @Override
    public List<Vec3> getVertexes() {
        return this.vertexes;
    }
    @Override
    public void draw(VertexBuilder builder) {
        RenderSystem.lineWidth(lineWidth);

        int n = vertexes.size();
        if (n < 2) return;

        builder.putColor(0x00000000F);
        Vec3 first = vertexes.getFirst();
        builder.putVertex((float) first.x, (float) first.y, (float) first.z, 0, 0, 0);

        for (int i = 0; i < n; i++) {
            Color vColor = color;
            if(i<vertexColors.size()){
                vColor = vertexColors.get(i);
            }
            builder.putColor(vColor);

            Vec3 normal;

            if (i == 0) {
                Vec3 dir = vertexes.get(i + 1).subtract(vertexes.get(i));
                normal = dir.normalize();
            } else if (i == n - 1) {
                Vec3 dir = vertexes.get(i).subtract(vertexes.get(i - 1));
                normal = dir.normalize();
            } else {
                Vec3 prevDir = vertexes.get(i).subtract(vertexes.get(i - 1));
                Vec3 nextDir = vertexes.get(i + 1).subtract(vertexes.get(i));
                normal = prevDir.add(nextDir).normalize();
            }

            Vec3 pos = vertexes.get(i);
            builder.putVertex((float) pos.x, (float) pos.y, (float) pos.z,
                    (float) normal.x, (float) normal.y, (float) normal.z);
        }
        builder.putColor(0x00000000F);
        Vec3 last = vertexes.getLast();
        builder.putVertex((float) last.x, (float) last.y, (float) last.z, 0, 0, 0);


    }

    @Override
    public void setLineWidth(float width) {
        this.lineWidth = width;
    }
}
