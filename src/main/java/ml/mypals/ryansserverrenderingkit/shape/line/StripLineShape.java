package ml.mypals.ryansserverrenderingkit.shape.line;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.collision.RayModelIntersection;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.core.StripLineLikeShape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    private List<Vec3> getWorldVertices() {
        if (modelVertexes.isEmpty()) {
            generateRawGeometry(false);
        }
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        List<Vec3> result = new ArrayList<>(modelVertexes.size());
        Vector3f v = new Vector3f();
        for (Vec3 local : modelVertexes) {
            v.set((float) local.x, (float) local.y, (float) local.z);
            rot.transform(v);
            result.add(new Vec3(center.x + v.x, center.y + v.y, center.z + v.z));
        }
        return result;
    }

    @Override
    public void initDisplays(ServerLevel level) {
        if (modelVertexes.isEmpty()) {
            generateRawGeometry(false);
        }
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = getLineWidth(false);
        int segCount = Math.max(0, modelVertexes.size() - 1);

        for (int i = 0; i < segCount; i++) {
            Vec3 p1 = modelVertexes.get(i);
            Vec3 p2 = modelVertexes.get(i + 1);
            Vector3f va = new Vector3f((float) p1.x, (float) p1.y, (float) p1.z);
            Vector3f vb = new Vector3f((float) p2.x, (float) p2.y, (float) p2.z);
            Transformation t = DisplayTransformHelper.localSegment(va, vb, width, rot, null);

            VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        if (modelVertexes.isEmpty()) {
            generateRawGeometry(true);
        }
        int segCount = Math.max(0, modelVertexes.size() - 1);
        if (this.displays.size() != segCount) {
            ServerLevel lvl = this.displays.isEmpty() ? this.level : this.displays.getFirst().getLevel();
            removeDisplays();
            if (lvl != null) {
                initDisplays(lvl);
            }
            return;
        }

        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = getLineWidth(true);

        VirtualDisplay first = this.displays.getFirst();
        Vec3 spawnPos = new Vec3(first.getEntity().getX(), first.getEntity().getY(), first.getEntity().getZ());
        Vec3 centerOffset = center.subtract(spawnPos);

        for (int i = 0; i < segCount; i++) {
            Vec3 p1 = modelVertexes.get(i);
            Vec3 p2 = modelVertexes.get(i + 1);
            Vector3f va = new Vector3f((float) p1.x, (float) p1.y, (float) p1.z);
            Vector3f vb = new Vector3f((float) p2.x, (float) p2.y, (float) p2.z);
            Transformation t = DisplayTransformHelper.localSegment(va, vb, width, rot, centerOffset);
            VirtualDisplay display = this.displays.get(i);
            display.blockState(getBlockState())
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
