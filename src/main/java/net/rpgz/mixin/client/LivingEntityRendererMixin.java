package net.rpgz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "getOverlayCoords", at = @At("HEAD"), cancellable = true)
    private static void getOverlayMixin(LivingEntity entity, float whiteOverlayProgress, CallbackInfoReturnable<Integer> info) {
        if (entity instanceof Mob) {
            info.setReturnValue(OverlayTexture.pack(OverlayTexture.u(whiteOverlayProgress), OverlayTexture.v(entity.hurtTime > 0)));
        }
    }

    @Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
    private void isShakingMixin(T entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof Mob && entity.isDeadOrDying()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "setupRotations", at = @At("HEAD"))
    private void setupTransformsMixin(T entity, PoseStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale, CallbackInfo info) {
        if (entity instanceof Mob && entity.deathTime > 0) {
            float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            if (f > 1.0F) {
                f = 1.0F;
            }
            float lyinganglebonus = 1F;
            if (this.getFlipDegrees(entity) > 90F) {
                lyinganglebonus = 2.5F;
            }
            matrices.translate(0.0D, (double) ((entity.getBbWidth() / 4.0D) * f) * lyinganglebonus, 0.0D);
            if (entity.isBaby()) {
                matrices.translate(-(double) ((entity.getBbHeight() / 2) * f), 0.0D, 0.0D);
            }
        }
    }

    @Inject(method = "getBob", at = @At("HEAD"), cancellable = true)
    private void getAnimationProgressMixin(T entity, float tickDelta, CallbackInfoReturnable<Float> info) {
        if (entity instanceof Mob && entity.isDeadOrDying()) {
            info.setReturnValue(0.0F);
        }
    }

    @Shadow
    protected abstract float getFlipDegrees(T entity);

}
