package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;

@Mixin(Phantom.class)
public abstract class PhantomEntityMixin extends Mob {
  public PhantomEntityMixin(EntityType<? extends Mob> entityType, Level world) {
    super(entityType, world);
  }

  @Inject(method = "aiStep", at = @At(value = "HEAD"), cancellable = true)
  public void livingTickMixinPhantom(CallbackInfo info) {
    if (this.deathTime > 0) {
      super.aiStep();
      info.cancel();
    }
  }

}