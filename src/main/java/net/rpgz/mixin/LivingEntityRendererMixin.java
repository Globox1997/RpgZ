package net.rpgz.mixin;

import java.util.List;

import com.google.common.collect.Lists;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.mob.CaveSpiderEntity;
// import net.minecraft.entity.mob.ElderGuardianEntity;
// import net.minecraft.entity.mob.EndermiteEntity;
// import net.minecraft.entity.mob.GuardianEntity;
// import net.minecraft.entity.mob.PathAwareEntity;
// import net.minecraft.entity.mob.SpiderEntity;
// import net.minecraft.entity.passive.CatEntity;
// import net.minecraft.entity.passive.DolphinEntity;
// import net.minecraft.entity.passive.FishEntity;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.particle.ParticleTypes;
// import net.minecraft.util.Formatting;
// import net.minecraft.util.Identifier;
// import net.minecraft.util.math.Box;
// import net.minecraft.util.math.Direction;
// import net.minecraft.util.math.MathHelper;
// import net.minecraft.util.math.Vec3d;

// import net.minecraft.client.render.entity.model.BlazeEntityModel;
// import net.minecraft.client.render.entity.SpiderEntityRenderer;
// import net.minecraft.client.render.entity.model.ZombieEntityModel;
// import net.minecraft.client.render.entity.model.GuardianEntityModel;
// import net.minecraft.client.render.entity.model.HorseEntityModel;
// import net.minecraft.client.render.entity.model.ParrotEntityModel;
// import net.minecraft.client.render.entity.model.ChickenEntityModel;
// import net.minecraft.client.render.entity.model.SquidEntityModel;
// import net.minecraft.client.render.entity.SquidEntityRenderer;
// import net.minecraft.client.render.entity.model.PiglinEntityModel;
// import net.minecraft.client.render.entity.model.PlayerEntityModel;
// import net.minecraft.entity.mob.BlazeEntity;
// import net.minecraft.entity.mob.VindicatorEntity;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.entity.mob.EvokerEntity;
// import net.minecraft.entity.passive.HorseEntity;
// import net.minecraft.entity.passive.FishEntity;
// import net.minecraft.entity.passive.ChickenEntity;
// import net.minecraft.entity.passive.VillagerEntity;
// import net.minecraft.entity.mob.PiglinEntity;
// import net.minecraft.entity.mob.FlyingEntity;
// import net.minecraft.entity.mob.GhastEntity;
// import net.minecraft.entity.mob.ZombieEntity;

// import net.minecraft.entity.passive.StriderEntity;
// import net.minecraft.entity.passive.BeeEntity;
// import net.minecraft.entity.passive.ParrotEntity;
// import net.minecraft.entity.mob.GuardianEntity;
// import net.minecraft.entity.mob.EndermanEntity;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.entity.mob.PiglinBrain;
// import net.minecraft.advancement.Advancement;

@Mixin(LivingEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
    extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

  @Shadow
  protected M model;
  @Shadow
  protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();

  public LivingEntityRendererMixin(EntityRenderDispatcher dispatcher, M model) {
    super(dispatcher);
    this.model = model;
  }

  @Overwrite
  public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
    return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0));
  }

  @Inject(method = "setupTransforms", at = @At("HEAD"), cancellable = true)
  public void setupTransformsMixin(T entity, MatrixStack matrices, float animationProgress, float bodyYaw,
      float tickDelta, CallbackInfo info) {
    if (entity.deathTime > 0) {
      this.shadowRadius = 0F;
      float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
      if (f > 1.0F) {
        f = 1.0F;
      }
      Float lyinganglebonus = 1F;
      if (this.getLyingAngle(entity) > 90F) {
        lyinganglebonus = 2.5F;
      }
      matrices.translate(0.0D, (double) ((entity.getWidth() / 4.0D) * f) * lyinganglebonus, 0.0D);
      if (entity.isBaby()) {
        matrices.translate(-(double) ((entity.getHeight() / 2) * f), 0.0D, // (double) -((entity.getHeight()) * f) *
            // lyinganglebonus
            0.0D);

      }
    }
  }

  @Redirect(method = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;isShaking(Lnet/minecraft/entity/LivingEntity;)Z"))
  public boolean isShakingMixin(LivingEntityRenderer<T, M> renderer, T entity, T secondentity, MatrixStack matrix,
      float o, float k, float m) {
    if (!entity.isDead() && this.isShaking(entity)) {
      return true;
    } else
      return false;
  }

  @Overwrite
  public float getAnimationProgress(T entity, float tickDelta) {
    if (entity.isDead()) {
      return 0.0F;
    } else
      return (float) entity.age + tickDelta;
  }

  @Shadow
  public M getModel() {
    return this.model;
  }

  @Shadow
  protected boolean isShaking(T entity) {
    return false;
  }

  @Shadow
  protected float getLyingAngle(T entity) {
    return 0F;
  }

}
