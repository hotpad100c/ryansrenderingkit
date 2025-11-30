package mypals.ml.mixin;

import mypals.ml.test.Debug;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin {
    @Inject(method = "addEntity", at = @At("HEAD"))
    public void addEntity(Entity entity, CallbackInfo ci) {
        Debug.addEntity(entity);
    }

    @Inject(method = "removeEntity", at = @At("HEAD"))
    public void removeEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
        Debug.removeEntity(entityId);
    }
}
