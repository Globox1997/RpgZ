package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

@Mixin(EnderMan.class)
public abstract class EndermanEntityMixin extends Monster {
  public EndermanEntityMixin(EntityType<? extends Monster> entityType, Level world) {
    super(entityType, world);
  }

  @Inject(method = "aiStep", at = @At(value = "HEAD"), cancellable = true)
  public void livingTickMixinEnderman(CallbackInfo info) {
    if (this.isDeadOrDying()) {
      super.aiStep();
      info.cancel();
    }
  }

}