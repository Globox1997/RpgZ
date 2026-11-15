package net.rpgz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void extractRenderStateMixin(T entity, S state, float partialTicks, CallbackInfo info) {
        if (entity instanceof Mob) {
            state.hasRedOverlay = entity.hurtTime > 0;
            if (entity.isDeadOrDying()) {
                state.ageInTicks = 0.0F;
            }
        }
    }

    @Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
    private void isShakingMixin(S state, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof MobRenderer && state.deathTime > 0.0F) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "setupRotations", at = @At("HEAD"))
    private void setupTransformsMixin(S state, PoseStack matrices, float bodyRot, float entityScale, CallbackInfo info) {
        if ((Object) this instanceof MobRenderer && state.deathTime > 0.0F) {
            float f = (state.deathTime - 1.0F) / 20.0F * 1.6F;
            if (f > 1.0F) {
                f = 1.0F;
            }
            float lyinganglebonus = 1F;
            if (this.getFlipDegrees() > 90F) {
                lyinganglebonus = 2.5F;
            }
            matrices.translate(0.0D, (double) ((state.boundingBoxWidth / 4.0D) * f) * lyinganglebonus, 0.0D);
            if (state.isBaby) {
                matrices.translate(-(double) ((state.boundingBoxHeight / 2) * f), 0.0D, 0.0D);
            }
        }
    }

    @Shadow
    protected abstract float getFlipDegrees();

}
