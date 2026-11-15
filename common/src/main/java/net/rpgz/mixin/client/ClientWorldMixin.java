package net.rpgz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin {

    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void tickEntityMixin(Entity entity, CallbackInfo info) {
        if (!entity.isAlive() && entity instanceof Mob) {
            entity.tickCount = -1;
        }
    }

}