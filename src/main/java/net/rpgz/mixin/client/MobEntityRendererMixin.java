package net.rpgz.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MobRenderer.class)
public abstract class MobEntityRendererMixin<T extends Mob> {

    @Inject(method = "getShadowRadius(Lnet/minecraft/world/entity/Mob;)F", at = @At("HEAD"), cancellable = true)
    private void getShadowRadiusMixin(T entity, CallbackInfoReturnable<Float> info) {
        if (entity.deathTime > 0) {
            info.setReturnValue(0.0F);
        }
    }

}
