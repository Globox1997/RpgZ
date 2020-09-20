package net.rpgz.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Mixin(EntityModel.class)
public abstract class EntityModelMixin<T extends Entity> extends Model {

  public EntityModelMixin(Function<Identifier, RenderLayer> layerFactory) {
    super(layerFactory);
  }

  // Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V

  // @Overwrite
  // public void setAngles(T entity, float limbAngle, float limbDistance, float
  // animationProgress, float headYaw,
  // float headPitch) {
  // if (entity instanceof LivingEntity) {
  // LivingEntity livingEntity = (LivingEntity) entity;
  // if (livingEntity.deathTime > 0) {
  // // animationProgress = 0.0f;
  // }
  // }
  // }

  // @Inject(method = "animateModel", at = @At(value = "RETURN"), cancellable = true)
  // public void animateModelMixin(T entity, float limbAngle, float limbDistance, float tickDelta, CallbackInfo info) {
  //   if (entity instanceof LivingEntity) {
  //     LivingEntity livingEntity = (LivingEntity) entity;
  //     if (livingEntity.deathTime > 0) {
  //       tickDelta = 0.0F;
  //       info.cancel();
  //     }
  //   }
  // }

}