package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(ClientLevel.class)
@OnlyIn(Dist.CLIENT)
public abstract class ClientWorldMixin {

  @Redirect(method = "Lnet/minecraft/client/multiplayer/ClientLevel;tickNonPassenger(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
  public void tickEntityMixin(Entity entity) {
    if (entity instanceof Mob) {
      if (!entity.isAlive()) {
        entity.tickCount = -1; // ticksExisted has to be 0 or -1 cause of some models (example: guardian)
      }
    }
    entity.tick();

  }

}