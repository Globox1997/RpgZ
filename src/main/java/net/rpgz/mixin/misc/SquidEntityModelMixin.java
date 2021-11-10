package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

@Mixin(SquidModel.class)
public abstract class SquidEntityModelMixin<T extends Entity> extends ListModel<T> {
  @Shadow
  private final ModelPart[] tentacles = new ModelPart[8];

  //I guess uneccessary
  @Inject(method = "setupAnim", at = @At(value = "RETURN"), cancellable = true)
  public void setRotationAnglesMixin(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
      float headPitch, CallbackInfo info) {
    if (!entity.isAlive()) {
      // ModelPart[] var7 = this.legs;
      // int var8 = var7.length;
      // for (int var9 = 0; var9 < var8; ++var9) {
      // ModelPart modelPart = var7[var9];
      // modelPart.pitch = 0.0F;
      // }
    }
  }

}