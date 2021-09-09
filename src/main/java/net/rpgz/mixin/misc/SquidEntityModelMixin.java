package net.rpgz.mixin.misc;

import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.Entity;

@Mixin(SquidEntityModel.class)
public abstract class SquidEntityModelMixin<T extends Entity> extends SinglePartEntityModel<T> {

    // Doesn't work for some reason
    @Inject(method = "setAngles", at = @At(value = "HEAD"), cancellable = true)
    public void setAnglesMixin(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo info) {
        // if (((SquidEntity) entity).isDead()) {
        // info.cancel();
        // }

    }

}