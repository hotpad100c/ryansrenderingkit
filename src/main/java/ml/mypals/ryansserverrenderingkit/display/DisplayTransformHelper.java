package ml.mypals.ryansserverrenderingkit.display;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class DisplayTransformHelper {
    public static final float DEFAULT_LINE_WIDTH = 0.05F;

    private DisplayTransformHelper() {}

    public static float sanitizeThickness(float thickness) {
        if (thickness <= 0.0001F) {
            return DEFAULT_LINE_WIDTH;
        }
        // In client-side OpenGL rendering, lineWidth was in pixels (typically 1.0F to 5.0F).
        // In Minecraft 3D block space, 1.0F is 1 full block (1 meter) thick!
        // Therefore, if thickness >= 0.5F, it represents legacy pixel/stroke width:
        // 1.0F -> 0.03F blocks, 2.0F -> 0.045F blocks, 3.0F -> 0.06F blocks, 4.0F -> 0.08F blocks.
        if (thickness >= 0.5F) {
            return Math.min(0.3F, thickness * 0.02F);
        }
        return thickness;
    }

    public static Transformation of(Vector3f translation, Quaternionf leftRotation, Vector3f scale, Quaternionf rightRotation) {
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }

    public static Transformation of(Vector3f translation, Quaternionf leftRotation, Vector3f scale) {
        return new Transformation(translation, leftRotation, scale, new Quaternionf());
    }

    public static Transformation identity() {
        return new Transformation(new org.joml.Matrix4f());
    }

    public static Transformation fromMatrix(org.joml.Matrix4f matrix) {
        return new Transformation(matrix);
    }

    @Nullable
    public static Transformation segment(Vec3 start, Vec3 end, float thickness) {
        thickness = sanitizeThickness(thickness);
        Vec3 direction = end.subtract(start);
        float length = (float) direction.length();

        if (length <= 0.0001F) {
            return null;
        }

        Vector3f dirNorm = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
        Quaternionf rotation = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, 1.0F), dirNorm);

        Vector3f offset = rotation.transform(new Vector3f(-thickness / 2.0F, -thickness / 2.0F, 0.0F), new Vector3f());

        return of(offset, rotation, new Vector3f(thickness, thickness, length));
    }

    @Nullable
    public static Transformation localSegment(Vector3f localA, Vector3f localB, float thickness, Quaternionf parentRot, @Nullable Vec3 centerOffset) {
        thickness = sanitizeThickness(thickness);
        Vector3f localDir = new Vector3f(localB).sub(localA);
        float length = localDir.length();
        if (length <= 0.0001F) {
            return null;
        }

        Vector3f dirNorm = new Vector3f(localDir).normalize();
        Quaternionf segRot = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, 1.0F), dirNorm);

        Vector3f cornerOffset = segRot.transform(new Vector3f(-thickness / 2.0F, -thickness / 2.0F, 0.0F), new Vector3f());
        Vector3f localPos = new Vector3f(localA).add(cornerOffset);

        Quaternionf totalRot = new Quaternionf(parentRot).mul(segRot);
        Vector3f worldTranslation = parentRot.transform(new Vector3f(localPos), new Vector3f());
        if (centerOffset != null) {
            worldTranslation.add((float) centerOffset.x, (float) centerOffset.y, (float) centerOffset.z);
        }

        return of(worldTranslation, totalRot, new Vector3f(thickness, thickness, length));
    }

    public static Transformation box(Vec3 min, Vec3 max) {
        float sizeX = (float) Math.abs(max.x - min.x);
        float sizeY = (float) Math.abs(max.y - min.y);
        float sizeZ = (float) Math.abs(max.z - min.z);
        return of(new Vector3f(), new Quaternionf(), new Vector3f(sizeX, sizeY, sizeZ));
    }

    public static Transformation centeredBox(Vec3 dimension, Quaternionf rotation, @Nullable Vec3 centerOffset) {
        float hx = (float) (dimension.x * 0.5);
        float hy = (float) (dimension.y * 0.5);
        float hz = (float) (dimension.z * 0.5);
        Vector3f offset = rotation.transform(new Vector3f(-hx, -hy, -hz), new Vector3f());
        if (centerOffset != null) {
            offset.add((float) centerOffset.x, (float) centerOffset.y, (float) centerOffset.z);
        }
        return of(offset, rotation, new Vector3f((float) dimension.x, (float) dimension.y, (float) dimension.z));
    }

    public static Transformation centeredBox(Vec3 dimension, Quaternionf rotation) {
        return centeredBox(dimension, rotation, null);
    }

    public static Transformation centeredBox(Vec3 dimension) {
        return centeredBox(dimension, new Quaternionf(), null);
    }

    public static Transformation entity(Entity entity, AABB box, double padding) {
        AABB inflated = box.inflate(padding);
        Vec3 at = entity.position();
        return of(
                new Vector3f(
                        (float) (inflated.minX - at.x),
                        (float) (inflated.minY - at.y),
                        (float) (inflated.minZ - at.z)),
                new Quaternionf(),
                new Vector3f((float) inflated.getXsize(), (float) inflated.getYsize(), (float) inflated.getZsize()));
    }
}
