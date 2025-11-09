package mypals.ml;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import static mypals.ml.RyansRenderingKit.MOD_ID;

public class Helpers {
    public static ResourceLocation generateUniqueId(String prefix) {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(10000);
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,prefix.toLowerCase() +"_"+ timestamp + "_" + randomNum);
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


    public static double maxComponent(Vec3 v) {
        return Math.max(Math.max(v.x, v.y), v.z);
    }

    public static double minComponent(Vec3 v) {
        return Math.min(Math.min(v.x, v.y), v.z);
    }
}
