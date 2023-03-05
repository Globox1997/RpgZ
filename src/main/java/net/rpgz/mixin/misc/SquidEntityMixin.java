package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

@Mixin(Squid.class)
public abstract class SquidEntityMixin extends WaterAnimal {

  public SquidEntityMixin(EntityType<? extends WaterAnimal> entityType, Level world) {
    super(entityType, world);
  }

  @Inject(method = "aiStep", at = @At(value = "HEAD"), cancellable = true)
  public void livingTickMixinSquid(CallbackInfo info) {
    if (this.isDeadOrDying()) {
      super.aiStep();
      info.cancel();
    }
  }

}