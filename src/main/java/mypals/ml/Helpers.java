package mypals.ml;

import net.minecraft.util.Identifier;

import java.util.concurrent.ThreadLocalRandom;

import static mypals.ml.RyansRenderingKit.MOD_ID;

public class Helpers {
    public static Identifier generateUniqueId(String prefix) {
        long timestamp = System.currentTimeMillis();
        int randomNum = ThreadLocalRandom.current().nextInt(10000);
        return Identifier.of(MOD_ID,prefix.toLowerCase() +"_"+ timestamp + "_" + randomNum);
    }
}
