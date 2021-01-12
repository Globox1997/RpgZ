package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.world.World;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity {
  @Shadow
  public float wingRotation;
  @Shadow
  public float destPos;
  @Shadow
  public float oFlapSpeed;
  @Shadow
  public float oFlap;

  public ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "livingTick", at = @At(value = "HEAD"), cancellable = true)
  public void livingTickChickenBlaze(CallbackInfo info) {
    if (this.getShouldBeDead()) {
      super.livingTick();
      this.wingRotation = 0.0F;
      this.destPos = 0.0F;
      this.oFlapSpeed = 0.0F;
      this.oFlap = 0.0F;
      info.cancel();
    }
  }

}