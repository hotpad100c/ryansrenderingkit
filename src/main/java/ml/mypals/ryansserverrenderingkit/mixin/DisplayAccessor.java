package ml.mypals.ryansserverrenderingkit.mixin;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker("setTransformation")
    void rrk$setTransformation(Transformation transformation);

    @Invoker("setViewRange")
    void rrk$setViewRange(float viewRange);

    @Invoker("setGlowColorOverride")
    void rrk$setGlowColorOverride(int glowColorOverride);

    @Invoker("setBrightnessOverride")
    void rrk$setBrightnessOverride(Brightness brightnessOverride);

    @Invoker("setBillboardConstraints")
    void rrk$setBillboardConstraints(Display.BillboardConstraints billboardConstraints);

    @Invoker("setInterpolationDuration")
    void rrk$setInterpolationDuration(int interpolationDuration);

    @Invoker("setInterpolationDelay")
    void rrk$setInterpolationDelay(int interpolationDelay);
}
