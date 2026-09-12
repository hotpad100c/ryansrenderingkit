package ml.mypals.ryansserverrenderingkit.mixin;

import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundSetPassengersPacket.class)
public interface SetPassengersPacketAccessor {
    @Mutable
    @Accessor("passengers")
    void rrk$setPassengers(int[] passengers);
}
