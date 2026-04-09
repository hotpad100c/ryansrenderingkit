package ml.mypals.ryansrenderingkit.shape.line;

import ml.mypals.ryansrenderingkit.builders.vertexBuilders.VertexBuilder;
import ml.mypals.ryansrenderingkit.collision.RayModelIntersection;
import ml.mypals.ryansrenderingkit.shape.Shape;
import ml.mypals.ryansrenderingkit.shape.basics.core.StripLineLikeShape;
import net.minecraft.world.phys.Vec3;
//? if <1.21.11 {
/*import com.mojang.blaze3d.systems.RenderSystem;
*///?}
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StripLineShape extends Shape implements StripLineLikeShape {

    private List<Vec3> vertexes = new ArrayList<>();
    private List<Color> vertexColors = new ArrayList<>();

    public StripLineShape(RenderingType type,
                          Consumer<SimpleLineTransformer> transform,
                          List<Vec3> vertexes,
                          float lineWidth,
                          Color color,
                          boolean seeThrough) {
        super(type, color, seeThrough);
        this.vertexes = new ArrayList<>(vertexes);
        this.transformer = new SimpleLineTransformer(this, lineWidth, this.calculateShapeCenterPos());
        this.transformFunction = (defaultTransformer) ->
                transform.accept((SimpleLineTransformer) this.transformer);

        syncLastToTarget();
        generateRawGeometry(false);
    }


    public Vec3 calculateShapeCenterPos() {
        if (vertexes.isEmpty()) return Vec3.ZERO;

        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3 v : vertexes) {
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
        }

        double n = vertexes.size();
        return new Vec3(sumX / n, sumY / n, sumZ / n);
    }

    @Override
    public RayModelIntersection.HitResult isPlayerLookingAt() {
        return new RayModelIntersection.HitResult(false, null, -1);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();
        if (vertexes.size() < 2) return;
        Vec3 localCenter = calculateShapeCenterPos();
        transformer.setShapeWorldPivot(localCenter);
        for (Vec3 v : vertexes) {
            modelVertexes.add(v.subtract(localCenter));
        }

        int n = modelVertexes.size();
        indexBuffer = new int[n];
        for (int i = 0; i < n; i++) indexBuffer[i] = i;
    }

    @Override
    protected void drawInternal(VertexBuilder builder) {
        float width = getLineWidth(true);
        //? if <1.21.11 {
        /*RenderSystem.lineWidth(getLineWidth(true));
        *///?}


        int n = modelVertexes.size();
        if (n < 2) return;

        Vec3 first = modelVertexes.getFirst();
        builder.putColor(new Color(0, 0, 0, 0));
        builder.putVertex(first, Vec3.ZERO,width);
        for (int i = 0; i < n; i++) {
            Color vColor = baseColor;
            if (i < vertexColors.size()) vColor = vertexColors.get(i);
            builder.putColor(vColor);
            Vec3 normal;
            if (i == 0) {
                Vec3 dir = modelVertexes.get(1).subtract(modelVertexes.get(0));
                normal = dir.normalize();
            } else if (i == n - 1) {
                Vec3 dir = modelVertexes.get(n - 1).subtract(modelVertexes.get(n - 2));
                normal = dir.normalize();
            } else {
                Vec3 prevDir = modelVertexes.get(i).subtract(modelVertexes.get(i - 1));
                Vec3 nextDir = modelVertexes.get(i + 1).subtract(modelVertexes.get(i));
                normal = prevDir.add(nextDir).normalize();
                if (Double.isNaN(normal.x) || Double.isNaN(normal.y) || Double.isNaN(normal.z)) {
                    Vec3 fallback = nextDir.lengthSqr() > 0 ? nextDir : prevDir;
                    normal = fallback.normalize();
                }
            }

            Vec3 pos = modelVertexes.get(i);
            builder.putVertex(pos, normal, width);
        }

        Vec3 last = modelVertexes.get(n - 1);
        builder.putColor(new Color(0, 0, 0, 0));
        builder.putVertex(last, Vec3.ZERO, width);
    }

    @Override
    public void setVertexes(List<Vec3> vertexes) {
        this.vertexes = new ArrayList<>(vertexes);
        generateRawGeometry(false);
    }

    @Override
    public List<Vec3> getVertexes() {
        return vertexes;
    }

    public void setVertexColors(List<Color> colors) {
        this.vertexColors = new ArrayList<>(colors);
    }
    public List<Color> getVertexColors() {return vertexColors;}

    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((SimpleLineTransformer) this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    @Override
    public void setLineWidth(float width) {
        ((SimpleLineTransformer) this.transformer).setWidth(width);
    }

    @Override
    public float getLineWidth(boolean lerp) {
        return ((SimpleLineTransformer) this.transformer).getWidth(lerp);
    }
}

