package mypals.ml.mixin;

import mypals.ml.test.Tester;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow @Final private ClientEntityManager<Entity> entityManager;

    @Shadow protected abstract EntityLookup<Entity> getEntityLookup();

    @Shadow public abstract void removeEntity(int entityId, Entity.RemovalReason removalReason);

    @Shadow public abstract void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter);

    @Inject(method = "addEntity",at=@At("HEAD"))
    public void addEntity(Entity entity, CallbackInfo ci) {
        Tester.addEntity(entity);
    }

    @Inject(method = "removeEntity",at=@At("HEAD"))
    public void removeEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
        Tester.removeEntity(entityId);
    }
}
