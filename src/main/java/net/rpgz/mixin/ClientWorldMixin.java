package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Redirect(method = "Lnet/minecraft/client/world/ClientWorld;tickEntity(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    public void tickEntityMixin(Entity entity) {
        if (entity instanceof MobEntity && !entity.isAlive())
            entity.age = -1; // age has to be 0 or -1 cause of some models (example: guardian)
        entity.tick();

    }

}