package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends HostileEntity {
  public EndermanEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tickMovement", at = @At(value = "HEAD"), cancellable = true)
  public void tickMovementMixinEnderman(CallbackInfo info) {
    if (this.isDead()) {
      super.tickMovement();
      info.cancel();
    }
  }

}