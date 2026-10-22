package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.rpgz.util.MobCorpse;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathMixin(DamageSource damageSource, CallbackInfo info) {
        if ((Object) this instanceof MobCorpse mobCorpse) {
            mobCorpse.onCorpseCreated();
        }
    }

    @Inject(method = "updatePostDeath", at = @At("HEAD"), cancellable = true)
    private void updatePostDeathMixin(CallbackInfo info) {
        if ((Object) this instanceof MobCorpse mobCorpse) {
            mobCorpse.tickCorpse();
            info.cancel();
        }
    }
}
