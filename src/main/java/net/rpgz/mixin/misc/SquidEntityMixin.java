package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.world.World;

@Mixin(SquidEntity.class)
public abstract class SquidEntityMixin extends WaterMobEntity {

  public SquidEntityMixin(EntityType<? extends WaterMobEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "livingTick", at = @At(value = "HEAD"), cancellable = true)
  public void livingTickMixinSquid(CallbackInfo info) {
    if (this.getShouldBeDead()) {
      super.livingTick();
      info.cancel();
    }
  }

}