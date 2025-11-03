package mypals.ml;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.resources.ResourceLocation;

import static mypals.ml.RyansRenderingKit.MOD_ID;

public class Helpers {
    public static ResourceLocation generateUniqueId(String prefix) {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(10000);
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,prefix.toLowerCase() +"_"+ timestamp + "_" + randomNum);
    }
}
