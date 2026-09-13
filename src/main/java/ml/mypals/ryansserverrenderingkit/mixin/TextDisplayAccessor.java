package ml.mypals.ryansserverrenderingkit.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface TextDisplayAccessor {


    @Invoker("setText")
    void rrk$setText(Component text);

    @Invoker("setLineWidth")
    void rrk$setLineWidth(int lineWidth);

    @Invoker("setTextOpacity")
    void rrk$setTextOpacity(byte textOpacity);

    @Invoker("setBackgroundColor")
    void rrk$setBackgroundColor(int backgroundColor);

    @Invoker("getBackgroundColor")
    int rrk$getBackgroundColor();

    @Invoker("setFlags")
    void rrk$setFlags(byte flags);

    @Invoker("getFlags")
    byte rrk$getFlags();
}
