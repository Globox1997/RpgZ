package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.world.World;

@Mixin(PhantomEntity.class)
public abstract class PhantomEntityMixin extends MobEntity {
  public PhantomEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tickMovement", at = @At(value = "HEAD"), cancellable = true)
  public void tickMovementMixinPhantom(CallbackInfo info) {
    if (this.deathTime > 0) {
      super.tickMovement();
      info.cancel();
    }
  }

}