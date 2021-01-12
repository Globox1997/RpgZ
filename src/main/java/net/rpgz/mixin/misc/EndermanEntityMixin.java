package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends MonsterEntity {
  public EndermanEntityMixin(EntityType<? extends MonsterEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "livingTick", at = @At(value = "HEAD"), cancellable = true)
  public void livingTickMixinEnderman(CallbackInfo info) {
    if (this.getShouldBeDead()) {
      super.livingTick();
      info.cancel();
    }
  }

}