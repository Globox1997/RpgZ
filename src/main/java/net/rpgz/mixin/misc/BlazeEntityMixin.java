package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;

@Mixin(BlazeEntity.class)
public abstract class BlazeEntityMixin extends MonsterEntity {
  public BlazeEntityMixin(EntityType<? extends MonsterEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "livingTick", at = @At(value = "HEAD"), cancellable = true)
  public void livingTickMixinBlaze(CallbackInfo info) {
    if (this.getShouldBeDead()) {
      super.livingTick();
      info.cancel();
    }
  }

}