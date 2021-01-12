package net.rpgz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.entity.model.SquidModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

@Mixin(SquidModel.class)
public abstract class SquidEntityModelMixin<T extends Entity> extends SegmentedModel<T> {
  @Shadow
  private final ModelRenderer[] legs = new ModelRenderer[8];

  @Inject(method = "setRotationAngles", at = @At(value = "RETURN"), cancellable = true)
  public void setRotationAnglesMixin(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
      float headPitch, CallbackInfo info) {
    if (!entity.isAlive()) {
      // ModelRenderer[] var7 = this.legs;
      // int var8 = var7.length;
      // for (int var9 = 0; var9 < var8; ++var9) {
      //   ModelRenderer modelPart = var7[var9];
      //   modelPart.pitch = 0.0F;
      // }
    }
  }

}