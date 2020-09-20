// package net.rpgz.mixin.misc;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// import org.spongepowered.asm.mixin.injection.At;

// import net.minecraft.client.render.entity.model.AnimalModel;
// import net.minecraft.client.render.entity.model.HorseEntityModel;
// import net.minecraft.entity.passive.HorseBaseEntity;

// @Mixin(HorseEntityModel.class)
// public abstract class HorseEntityModelMixin<T extends HorseBaseEntity>
// extends AnimalModel<T> {

// @Inject(method = "animateModel", at = @At(value = "HEAD"), cancellable =
// true)
// private void animateModelMixin(T horseBaseEntity, float f, float g, float h,
// CallbackInfo info) {
// if (horseBaseEntity.deathTime > 0) {
// info.cancel();
// }
// }

// }