package net.rpgz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "getOverlay", at = @At("HEAD"), cancellable = true)
    private static void getOverlayMixin(LivingEntity entity, float whiteOverlayProgress, CallbackInfoReturnable<Integer> info) {
        if (entity instanceof MobEntity) {
            info.setReturnValue(OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0)));
        }
    }

    @Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
    private void isShakingMixin(T entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof MobEntity && entity.isDead()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "setupTransforms", at = @At("HEAD"))
    private void setupTransformsMixin(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale, CallbackInfo info) {
        if (entity instanceof MobEntity && entity.deathTime > 0) {
            float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            if (f > 1.0F) {
                f = 1.0F;
            }
            float lyinganglebonus = 1F;
            if (this.getLyingAngle(entity) > 90F) {
                lyinganglebonus = 2.5F;
            }
            matrices.translate(0.0D, (double) ((entity.getWidth() / 4.0D) * f) * lyinganglebonus, 0.0D);
            if (entity.isBaby()) {
                matrices.translate(-(double) ((entity.getHeight() / 2) * f), 0.0D, 0.0D);
            }
        }
    }

    @Inject(method = "getAnimationProgress", at = @At("HEAD"), cancellable = true)
    private void getAnimationProgressMixin(T entity, float tickDelta, CallbackInfoReturnable<Float> info) {
        if (entity instanceof MobEntity && entity.isDead()) {
            info.setReturnValue(0.0F);
        }
    }

    @Shadow
    protected abstract float getLyingAngle(T entity);

}
