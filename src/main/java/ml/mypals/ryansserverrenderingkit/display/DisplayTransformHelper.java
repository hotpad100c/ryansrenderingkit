package ml.mypals.ryansserverrenderingkit.display;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class DisplayTransformHelper {
    public static final float DEFAULT_LINE_WIDTH = 0.05F;
    private static final Matrix4f TEXT_DISPLAY_UNIT_SQUARE = new Matrix4f()
            .translate(0.4F, 0.0F, 0.0F)
            .scale(8.0F, 4.0F, 1.0F);
    private static final Matrix4f[] TEXT_DISPLAY_UNIT_TRIANGLE = {
            new Matrix4f().scale(0.5F).mul(TEXT_DISPLAY_UNIT_SQUARE),
            new Matrix4f().scale(0.5F).translate(1.0F, 0.0F, 0.0F)
                    .mul(shear(0.0F, -1.0F)).mul(TEXT_DISPLAY_UNIT_SQUARE),
            new Matrix4f().scale(0.5F).translate(0.0F, 1.0F, 0.0F)
                    .mul(shear(-1.0F, 0.0F)).mul(TEXT_DISPLAY_UNIT_SQUARE)
    };

    private DisplayTransformHelper() {}

    private static Matrix4f shear(float xy, float yx) {
        return new Matrix4f(
                1.0F, xy, 0.0F, 0.0F,
                yx, 1.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 1.0F, 0.0F,
                0.0F, 0.0F, 0.0F, 1.0F
        );
    }

    public static float sanitizeThickness(float thickness) {
        if (thickness <= 0.0001F) {
            return DEFAULT_LINE_WIDTH;
        }
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
    public static Transformation localTextPanel(Vector3f localOrigin, Vector3f localX, Vector3f localY,
                                                Quaternionf parentRot, @Nullable Vec3 centerOffset) {
        Vector3f normal = new Vector3f(localX).cross(localY);
        if (localX.lengthSquared() <= 0.00000001F || localY.lengthSquared() <= 0.00000001F
                || normal.lengthSquared() <= 0.00000001F) {
            return null;
        }
        normal.normalize();

        Matrix4f panel = new Matrix4f(
                localX.x, localX.y, localX.z, 0.0F,
                localY.x, localY.y, localY.z, 0.0F,
                normal.x, normal.y, normal.z, 0.0F,
                localOrigin.x, localOrigin.y, localOrigin.z, 1.0F
        ).mul(TEXT_DISPLAY_UNIT_SQUARE);

        Matrix4f result = new Matrix4f();
        if (centerOffset != null) {
            result.translate((float) centerOffset.x, (float) centerOffset.y, (float) centerOffset.z);
        }
        result.rotate(parentRot).mul(panel);
        return fromMatrix(result);
    }

    /** Returns the three TextDisplay transforms that exactly tile an arbitrary local-space triangle. */
    public static List<Transformation> localTextTriangle(Vector3f p1, Vector3f p2, Vector3f p3,
                                                         Quaternionf parentRot, @Nullable Vec3 centerOffset) {
        Matrix4f parent = new Matrix4f();
        if (centerOffset != null) {
            parent.translate((float) centerOffset.x, (float) centerOffset.y, (float) centerOffset.z);
        }
        parent.rotate(parentRot);
        return localTextTriangle(p1, p2, p3, parent);
    }

    /** Returns three triangle pieces after applying a complete parent/model matrix. */
    public static List<Transformation> localTextTriangle(Vector3f p1, Vector3f p2, Vector3f p3,
                                                         Matrix4f parentTransform) {
        Vector3f x = new Vector3f(p2).sub(p1);
        Vector3f y = new Vector3f(p3).sub(p1);
        Vector3f normal = new Vector3f(x).cross(y);
        if (x.lengthSquared() <= 0.00000001F || y.lengthSquared() <= 0.00000001F
                || normal.lengthSquared() <= 0.00000001F) {
            return List.of();
        }
        normal.normalize();

        Matrix4f triangle = new Matrix4f(
                x.x, x.y, x.z, 0.0F,
                y.x, y.y, y.z, 0.0F,
                normal.x, normal.y, normal.z, 0.0F,
                p1.x, p1.y, p1.z, 1.0F
        );
        Matrix4f parent = new Matrix4f(parentTransform).mul(triangle);

        List<Transformation> result = new ArrayList<>(3);
        for (Matrix4f piece : TEXT_DISPLAY_UNIT_TRIANGLE) {
            result.add(fromMatrix(new Matrix4f(parent).mul(piece)));
        }
        return result;
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
