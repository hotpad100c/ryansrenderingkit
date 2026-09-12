package ml.mypals.ryansserverrenderingkit.shape.line;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.collision.RayModelIntersection;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.core.StripLineLikeShape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StripLineShape extends Shape implements StripLineLikeShape {

    private List<Vec3> vertexes = new ArrayList<>();
    private List<Color> vertexColors = new ArrayList<>();

    public StripLineShape(Consumer<SimpleLineTransformer> transform,
                          List<Vec3> vertexes,
                          float lineWidth,
                          Color color,
                          boolean seeThrough) {
        super(color, seeThrough);
        this.vertexes = new ArrayList<>(vertexes);
        this.transformer = new SimpleLineTransformer(this, lineWidth, this.calculateShapeCenterPos());
        this.transformFunction = (defaultTransformer) -> {
            if (transform != null) {
                transform.accept((SimpleLineTransformer) this.transformer);
            }
        };

        syncLastToTarget();
        generateRawGeometry(false);
    }

    @Deprecated
    public StripLineShape(Object ignored,
                          Consumer<SimpleLineTransformer> transform,
                          List<Vec3> vertexes,
                          float lineWidth,
                          Color color,
                          boolean seeThrough) {
        this(transform, vertexes, lineWidth, color, seeThrough);
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
    public void initDisplays(ServerLevel level) {
        float width = getLineWidth(false);
        int segCount = Math.max(0, vertexes.size() - 1);
        for (int i = 0; i < segCount; i++) {
            Vec3 a = vertexes.get(i);
            Vec3 b = vertexes.get(i + 1);
            VirtualDisplay display = VirtualDisplay.block(level, a.x, a.y, a.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(DisplayTransformHelper.segment(a, b, width));
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        int segCount = Math.max(0, vertexes.size() - 1);
        if (this.displays.size() != segCount) {
            ServerLevel lvl = this.displays.isEmpty() ? this.level : this.displays.getFirst().getLevel();
            removeDisplays();
            if (lvl != null) {
                initDisplays(lvl);
            }
            return;
        }

        float width = getLineWidth(true);
        for (int i = 0; i < segCount; i++) {
            Vec3 a = vertexes.get(i);
            Vec3 b = vertexes.get(i + 1);
            Transformation t = DisplayTransformHelper.segment(a, b, width);
            VirtualDisplay display = this.displays.get(i);
            display.pos(a.x, a.y, a.z)
                    .blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }
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

    public List<Color> getVertexColors() { return vertexColors; }

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
