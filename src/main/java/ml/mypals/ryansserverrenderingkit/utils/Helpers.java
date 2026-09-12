package ml.mypals.ryansserverrenderingkit.utils;

import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static ml.mypals.ryansserverrenderingkit.RyansServerRenderingKit.MOD_ID;

public class Helpers {
    public static Identifier generateUniqueId(String prefix) {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(10000);
        return Identifier.fromNamespaceAndPath(MOD_ID, prefix.toLowerCase() + "_" + timestamp + "_" + randomNum);
    }

    public static Vec3 max(Vec3 a, Vec3 b) {
        return new Vec3(
                Math.max(a.x, b.x),
                Math.max(a.y, b.y),
                Math.max(a.z, b.z)
        );
    }

    public static Vec3 min(Vec3 a, Vec3 b) {
        return new Vec3(
                Math.min(a.x, b.x),
                Math.min(a.y, b.y),
                Math.min(a.z, b.z)
        );
    }

    public static Vec3 calculateCentroid(List<Vec3> vertices) {
        double sumX = 0, sumY = 0, sumZ = 0;
        for (Vec3 v : vertices) {
            sumX += v.x;
            sumY += v.y;
            sumZ += v.z;
        }
        double n = vertices.size();
        return new Vec3(sumX / n, sumY / n, sumZ / n);
    }

    public static int multiplyRGB(int color, float shade) {
        int alpha = color >>> 24 & 255;
        int red = (int) ((float) (color >>> 16 & 255) * shade);
        int green = (int) ((float) (color >>> 8 & 255) * shade);
        int blue = (int) ((float) (color & 255) * shade);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static Matrix4f convertToMojangIfNeeded(Matrix4f matrix4f2) {
        return matrix4f2;
    }

    public static org.joml.Matrix4f convertToJomlIfNeeded(org.joml.Matrix4f matrix4f) {
        return matrix4f;
    }
}
