package mypals.ml.shape.box;

import mypals.ml.shape.Shape;
import mypals.ml.shape.basics.BoxLikeShape;
import net.minecraft.world.phys.Vec3;
import java.util.function.BiConsumer;

public class BoxShape extends Shape implements BoxLikeShape {
    private Vec3 center;
    private Vec3 dimensions;
    private Vec3 min;
    private Vec3 max;

    public enum BoxConstructionType {
        CENTER_AND_DIMENSIONS,
        CORNERS
    }

    public BoxShape(RenderingType type, BiConsumer<BoxTransformer, Shape> transform,
                    Vec3 vec1, Vec3 vec2, boolean seeThrough, BoxConstructionType constructionType) {
        super(type, seeThrough);
        this.transformer = new BoxTransformer(this);
        this.transformFunction = (defaultTransformer, shape) -> transform.accept((BoxTransformer) this.transformer, shape);

        if (constructionType == BoxConstructionType.CENTER_AND_DIMENSIONS) {
            this.center = vec1;
            this.dimensions = new Vec3(Math.abs(vec2.x), Math.abs(vec2.y), Math.abs(vec2.z));
        } else {

            this.dimensions = new Vec3(
                    Math.abs(vec2.x - vec1.x),
                    Math.abs(vec2.y - vec1.y),
                    Math.abs(vec2.z - vec1.z)
            );
            this.center = new Vec3(
                    (vec1.x + vec2.x) / 2.0,
                    (vec1.y + vec2.y) / 2.0,
                    (vec1.z + vec2.z) / 2.0
            );
        }
        this.transformer.setShapeCenterPos(this.center);
        ((BoxTransformer)this.transformer).setDimension(this.dimensions);
        updateCornersFromCenterAndDimensions();
        normalizeBounds();
        syncLastToTarget();
    }

    public BoxShape(RenderingType type, BiConsumer<BoxTransformer, Shape> transform,
                    Vec3 vec1, Vec3 vec2, BoxConstructionType constructionType) {
        this(type, transform, vec1, vec2, false, constructionType);
    }

    @Override
    public Vec3 getMin() {
        return this.min;
    }

    @Override
    public Vec3 getMax() {
        return this.max;
    }

    @Override
    public void setMin(Vec3 min) {
        this.min = min;
        this.center = new Vec3(
                (min.x + max.x) / 2.0,
                (min.y + max.y) / 2.0,
                (min.z + max.z) / 2.0
        );
        this.dimensions = new Vec3(
                max.x - min.x,
                max.y - min.y,
                max.z - min.z
        );
    }

    @Override
    public void setMax(Vec3 max) {
        this.max = max;
        this.center = new Vec3(
                (min.x + max.x) / 2.0,
                (min.y + max.y) / 2.0,
                (min.z + max.z) / 2.0
        );
        this.dimensions = new Vec3(
                max.x - min.x,
                max.y - min.y,
                max.z - min.z
        );
    }

    public Vec3 getCenter() {
        return this.center;
    }

    public Vec3 getDimensions() {
        return this.dimensions;
    }

    @Override
    public void setShapeCenterPos(Vec3 center) {
        this.center = center;
        this.centerPoint = center;
        updateCornersFromCenterAndDimensions();
    }

    public void setDimensions(Vec3 dimensions) {
        this.dimensions = new Vec3(Math.abs(dimensions.x), Math.abs(dimensions.y), Math.abs(dimensions.z));
        updateCornersFromCenterAndDimensions();
    }

    private void updateCornersFromCenterAndDimensions() {
        Vec3 half = dimensions.scale(0.5);
        this.min = center.subtract(half);
        this.max = center.add(half);
    }

    public void setCorners(Vec3 min, Vec3 max) {
        Vec3 dims = new Vec3(
                Math.abs(max.x - min.x),
                Math.abs(max.y - min.y),
                Math.abs(max.z - min.z)
        );
        Vec3 c = new Vec3(
                (min.x + max.x) / 2.0,
                (min.y + max.y) / 2.0,
                (min.z + max.z) / 2.0
        );
        setShapeCenterPos(c);
        setDimensions(dims);
    }

    @Override
    public void normalizeBounds() {
        // 保证 min/max 永远小于等于 max
        double minX = Math.min(min.x, max.x);
        double minY = Math.min(min.y, max.y);
        double minZ = Math.min(min.z, max.z);
        double maxX = Math.max(min.x, max.x);
        double maxY = Math.max(min.y, max.y);
        double maxZ = Math.max(min.z, max.z);
        this.min = new Vec3(minX, minY, minZ);
        this.max = new Vec3(maxX, maxY, maxZ);
    }

    @Override
    public Vec3 calculateShapeCenterPos() {
        return this.center;
    }
}
