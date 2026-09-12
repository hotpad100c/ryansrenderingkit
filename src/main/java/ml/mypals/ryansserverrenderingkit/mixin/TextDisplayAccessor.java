package ml.mypals.ryansserverrenderingkit.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface TextDisplayAccessor {
    byte FLAG_SHADOW = 1;
    byte FLAG_SEE_THROUGH = 2;
    byte FLAG_USE_DEFAULT_BACKGROUND = 4;
    byte FLAG_ALIGN_LEFT = 8;
    byte FLAG_ALIGN_RIGHT = 16;

    @Invoker("setText")
    void rrk$setText(Component text);

    @Invoker("setLineWidth")
    void rrk$setLineWidth(int lineWidth);

    @Invoker("setTextOpacity")
    void rrk$setTextOpacity(byte textOpacity);

    @Invoker("setBackgroundColor")
    void rrk$setBackgroundColor(int backgroundColor);

    @Invoker("setFlags")
    void rrk$setFlags(byte flags);

    @Invoker("getFlags")
    byte rrk$getFlags();
}
