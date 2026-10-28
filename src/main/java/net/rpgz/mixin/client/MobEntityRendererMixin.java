package net.rpgz.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MobRenderer.class)
public abstract class MobEntityRendererMixin<S extends LivingEntityRenderState> {

    @Inject(method = "getShadowRadius(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)F", at = @At("HEAD"), cancellable = true)
    private void getShadowRadiusMixin(S state, CallbackInfoReturnable<Float> info) {
        if (state.deathTime > 0.0F) {
            info.setReturnValue(0.0F);
        }
    }

}
