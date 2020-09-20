package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.Entity;

@Mixin(SquidEntityModel.class)
public abstract class SquidEntityModelMixin<T extends Entity> extends CompositeEntityModel<T> {
  @Shadow
  private final ModelPart[] field_3574 = new ModelPart[8];

  @Inject(method = "setAngles", at = @At(value = "RETURN"), cancellable = true)
  public void setAnglesMixin(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
      float headPitch, CallbackInfo info) {
    if (!entity.isAlive()) {
      // ModelPart[] var7 = this.field_3574;
      // int var8 = var7.length;
      // for (int var9 = 0; var9 < var8; ++var9) {
      //   ModelPart modelPart = var7[var9];
      //   modelPart.pitch = 0.0F;
      // }
    }
  }

}