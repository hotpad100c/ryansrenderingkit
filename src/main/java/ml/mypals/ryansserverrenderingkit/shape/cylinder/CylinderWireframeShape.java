package ml.mypals.ryansserverrenderingkit.shape.cylinder;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.display.DisplayTransformHelper;
import ml.mypals.ryansserverrenderingkit.display.VirtualDisplay;
import ml.mypals.ryansserverrenderingkit.shape.basics.core.LineLikeShape;
import ml.mypals.ryansserverrenderingkit.shape.basics.tags.DrawableLine;
import ml.mypals.ryansserverrenderingkit.transform.shapeTransformers.shapeModelInfoTransformer.LineModelInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CylinderWireframeShape extends CylinderShape implements DrawableLine, LineLikeShape {

    public CylinderWireframeShape(Consumer<CylinderWireframeTransformer> transform,
                                  CircleAxis circleAxis, Vec3 center, int segments,
                                  float radius, float height, float lineWidth, Color color, boolean seeThrough) {
        super(seeThrough);
        this.baseColor = color != null ? color : Color.WHITE;
        this.transformer = new CylinderWireframeTransformer(this, segments, radius, height, lineWidth, center);
        this.transformFunction = t -> {
            if (transform != null) {
                transform.accept((CylinderWireframeTransformer) this.transformer);
            }
        };

        this.setAxis(circleAxis);
        this.setWorldPosition(center);
        syncLastToTarget();
    }

    @Deprecated
    public CylinderWireframeShape(Object ignored, Consumer<CylinderWireframeTransformer> transform,
                                  CircleAxis circleAxis, Vec3 center, int segments,
                                  float radius, float height, float lineWidth, Color color, boolean seeThrough) {
        this(transform, circleAxis, center, segments, radius, height, lineWidth, color, seeThrough);
    }

    @Override
    public void initDisplays(ServerLevel level) {
        List<Vector3f[]> edges = getLocalCylinderEdges(false);
        Vec3 center = transformer.getWorldPivot();
        Quaternionf rot = transformer.getWorldRotation();
        float width = getLineWidth(false);

        for (Vector3f[] edge : edges) {
            Transformation t = DisplayTransformHelper.localSegment(edge[0], edge[1], width, rot, null);
            VirtualDisplay display = VirtualDisplay.block(level, center.x, center.y, center.z, getBlockState())
                    .bright()
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
            this.displays.add(display);
        }
    }

    @Override
    public void updateDisplays() {
        List<Vector3f[]> edges = getLocalCylinderEdges(true);
        if (this.displays.size() != edges.size()) {
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

        for (int i = 0; i < edges.size(); i++) {
            Vector3f[] edge = edges.get(i);
            Transformation t = DisplayTransformHelper.localSegment(edge[0], edge[1], width, rot, centerOffset);
            VirtualDisplay display = this.displays.get(i);
            display.blockState(getBlockState())
                    .seeThrough(this.seeThrough, this.baseColor.getRGB())
                    .transform(t);
        }
    }

    @Override
    public void setLineWidth(float width) {
        ((CylinderWireframeTransformer) this.transformer).setWidth(width);
    }

    @Override
    public float getLineWidth(boolean lerp) {
        return ((CylinderWireframeTransformer) this.transformer).getWidth(lerp);
    }

    public static class CylinderWireframeTransformer extends CylinderTransformer {
        private final LineModelInfo lineModelInfo;

        public CylinderWireframeTransformer(CylinderWireframeShape managedShape, int segments, float radius, float height, float width, Vec3 center) {
            super(managedShape, segments, radius, height, center);
            this.lineModelInfo = new LineModelInfo(width);
        }

        public void setWidth(float width) {
            lineModelInfo.setWidth(width);
        }

        public float getWidth(boolean lerp) {
            return lineModelInfo.getWidth(lerp);
        }

        @Override
        public void updateTickDelta(float delta) {
            super.updateTickDelta(delta);
            lineModelInfo.update(delta);
        }

        @Override
        public void syncLastToTarget() {
            super.syncLastToTarget();
            lineModelInfo.syncLastToTarget();
        }

        @Override
        public boolean asyncModelInfo() {
            return super.asyncModelInfo() || lineModelInfo.async();
        }
    }
}
