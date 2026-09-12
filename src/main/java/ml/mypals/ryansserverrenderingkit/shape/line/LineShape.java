package ml.mypals.ryansserverrenderingkit.shape.line;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.Shape;
import ml.mypals.ryansserverrenderingkit.shape.basics.core.TwoPointsLineShape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.function.Consumer;

public class LineShape extends Shape implements TwoPointsLineShape {

    public LineShape(Consumer<TwoPointsLineTransformer> transform,
                     Vec3 start, Vec3 end,
                     Color color, float lineWidth,
                     boolean seeThrough) {
        super(color, seeThrough);

        this.transformer = new TwoPointsLineTransformer(this, start, end, lineWidth, Vec3.ZERO);
        this.transformFunction = (defaultTransformer) -> {
            if (transform != null) {
                transform.accept((TwoPointsLineTransformer) this.transformer);
            }
        };

        syncLastToTarget();
        generateRawGeometry(false);
        this.transformer.setShapeWorldPivot(calculateShapeCenterPos());
    }

    @Deprecated
    public LineShape(Object ignored,
                     Consumer<TwoPointsLineTransformer> transform,
                     Vec3 start, Vec3 end,
                     Color color, float lineWidth,
                     boolean seeThrough) {
        this(transform, start, end, color, lineWidth, seeThrough);
    }

    public Vec3 calculateShapeCenterPos() {
        double centerX = (getStart(false).x + getEnd(false).x) / 2.0;
        double centerY = (getStart(false).y + getEnd(false).y) / 2.0;
        double centerZ = (getStart(false).z + getEnd(false).z) / 2.0;
        return new Vec3(centerX, centerY, centerZ);
    }

    public void forceSetStart(Vec3 start) {
        setStart(start);
        ((TwoPointsLineTransformer) this.transformer).lineModelInfo.startPointTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetEnd(Vec3 end) {
        setEnd(end);
        ((TwoPointsLineTransformer) this.transformer).lineModelInfo.endPointTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    public void forceSetLineWidth(float width) {
        setLineWidth(width);
        ((TwoPointsLineTransformer) this.transformer).lineModelInfo.widthTransformer.syncLastToTarget();
        generateRawGeometry(false);
    }

    @Override
    public void setStart(Vec3 start) {
        ((TwoPointsLineTransformer) this.transformer).setStart(start);
    }

    @Override
    public void setEnd(Vec3 end) {
        ((TwoPointsLineTransformer) this.transformer).setEnd(end);
    }

    @Override
    public void setLineWidth(float width) {
        ((TwoPointsLineTransformer) this.transformer).setWidth(width);
    }

    @Override
    public float getLineWidth(boolean lerp) {
        return ((TwoPointsLineTransformer) this.transformer).getWidth(lerp);
    }

    @Override
    public Vec3 getStart(boolean lerp) {
        return ((TwoPointsLineTransformer) this.transformer).getStart(lerp);
    }

    @Override
    public Vec3 getEnd(boolean lerp) {
        return ((TwoPointsLineTransformer) this.transformer).getEnd(lerp);
    }

    public float getWidth(boolean lerp) {
        return ((TwoPointsLineTransformer) this.transformer).getWidth(lerp);
    }

    @Override
    protected void generateRawGeometry(boolean lerp) {
        modelVertexes.clear();

        Vec3 start = getStart(lerp);
        Vec3 end = getEnd(lerp);
        Vec3 center = calculateShapeCenterPos();

        this.transformer.setShapeWorldPivot(center);
        this.transformer.world.position.syncLastToTarget();

        Vec3 localA = start.subtract(center);
        Vec3 localB = end.subtract(center);

        modelVertexes.add(localA);
        modelVertexes.add(localB);

        this.indexBuffer = new int[]{0, 1};
    }

    @Override
    public void syncLastToTarget() {
        super.syncLastToTarget();
        generateRawGeometry(false);
    }

    private Vec3[] getWorldPoints(boolean lerp) {
        Vec3 start = getStart(lerp);
        Vec3 end = getEnd(lerp);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();

        Vec3 rawCenter = calculateShapeCenterPos();
        Vec3 localA = start.subtract(rawCenter);
        Vec3 localB = end.subtract(rawCenter);

        Vector3f va = new Vector3f((float) localA.x, (float) localA.y, (float) localA.z);
        Vector3f vb = new Vector3f((float) localB.x, (float) localB.y, (float) localB.z);
        rot.transform(va);
        rot.transform(vb);

        Vec3 pA = new Vec3(center.x + va.x, center.y + va.y, center.z + va.z);
        Vec3 pB = new Vec3(center.x + vb.x, center.y + vb.y, center.z + vb.z);
        return new Vec3[]{pA, pB};
    }

    @Override
    public void initDisplays(ServerLevel level) {
        Vec3 start = getStart(false);
        Vec3 end = getEnd(false);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();

        Vec3 rawCenter = calculateShapeCenterPos();
        Vec3 localA = start.subtract(rawCenter);
        Vec3 localB = end.subtract(rawCenter);

        Vector3f va = new Vector3f((float) localA.x, (float) localA.y, (float) localA.z);
        Vector3f vb = new Vector3f((float) localB.x, (float) localB.y, (float) localB.z);

        float w = getLineWidth(false);
        Transformation transform = DisplayTransformHelper.localSegment(va, vb, w, rot, null);

        VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                .bright()
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(transform);
        this.displays.add(display);
    }

    @Override
    public void updateDisplays() {
        if (this.displays.isEmpty()) return;
        Vec3 start = getStart(true);
        Vec3 end = getEnd(true);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();

        Vec3 rawCenter = calculateShapeCenterPos();
        Vec3 localA = start.subtract(rawCenter);
        Vec3 localB = end.subtract(rawCenter);

        Vector3f va = new Vector3f((float) localA.x, (float) localA.y, (float) localA.z);
        Vector3f vb = new Vector3f((float) localB.x, (float) localB.y, (float) localB.z);

        float w = getLineWidth(true);

        VirtualDisplay display = this.displays.getFirst();
        Vec3 spawnPos = new Vec3(display.getEntity().getX(), display.getEntity().getY(), display.getEntity().getZ());
        Vec3 centerOffset = center.subtract(spawnPos);

        Transformation transform = DisplayTransformHelper.localSegment(va, vb, w, rot, centerOffset);

        display.blockState(getBlockState())
                .seeThrough(this.seeThrough, this.baseColor.getRGB())
                .transform(transform);
    }
}
