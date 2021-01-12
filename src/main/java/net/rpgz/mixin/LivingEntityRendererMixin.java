package net.rpgz.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(LivingRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
    extends EntityRenderer<T> implements IEntityRenderer<T, M> {

  @Shadow
  protected M entityModel;
  @Shadow
  protected final List<LayerRenderer<T, M>> layerRenderers = Lists.newArrayList();

  public LivingEntityRendererMixin(EntityRendererManager dispatcher, M entityModel) {
    super(dispatcher);
    this.entityModel = entityModel;
  }

  @Overwrite
  public static int getPackedOverlay(LivingEntity entity, float whiteOverlayProgress) {
    return OverlayTexture.getPackedUV(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0));
  }

  @Inject(method = "applyRotations", at = @At("HEAD"), cancellable = true)
  public void applyRotationsMixin(T entity, MatrixStack matrices, float animationProgress, float bodyYaw,
      float tickDelta, CallbackInfo info) {
    if (entity.deathTime > 0) {
      this.shadowSize = 0F;
      float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
      if (f > 1.0F) {
        f = 1.0F;
      }
      Float lyinganglebonus = 1F;
      if (this.getDeathMaxRotation(entity) > 90F) {
        lyinganglebonus = 2.5F;
      }
      matrices.translate(0.0D, (double) ((entity.getWidth() / 4.0D) * f) * lyinganglebonus, 0.0D);
      if (entity.isChild()) {
        matrices.translate(-(double) ((entity.getHeight() / 2) * f), 0.0D, // (double) -((entity.getHeight()) * f) *
            // lyinganglebonus
            0.0D);

      }
    }
  }

  @Redirect(method = "Lnet/minecraft/client/renderer/entity/LivingRenderer;applyRotations(Lnet/minecraft/entity/LivingEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingRenderer;func_230495_a_"/*isShaking*/+"(Lnet/minecraft/entity/LivingEntity;)Z"))
  public boolean isShakingMixin(LivingRenderer<T, M> renderer, T entity, T secondentity, MatrixStack matrix,
      float o, float k, float m) {
    if (!entity.getShouldBeDead() && this.func_230495_a_/*isShaking*/(entity)) {
      return true;
    } else
      return false;
  }

  @Overwrite
  public float handleRotationFloat(T entity, float tickDelta) {
    if (entity.getShouldBeDead()) {
      return 0.0F;
    } else
      return (float) entity.ticksExisted + tickDelta;
  }

  @Shadow
  public M getEntityModel() {
    return this.entityModel;
  }

  @Shadow
  protected boolean func_230495_a_/*isShaking*/(T entity) {
    return false;
  }

  @Shadow
  protected float getDeathMaxRotation(T entity) {
    return 0F;
  }

}
