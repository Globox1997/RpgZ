package net.rpgz.mixin;

import java.util.List;

import com.google.common.collect.Lists;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.ZombieEntity;

import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.advancement.Advancement;

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

  // @Redirect(method =
  // "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
  // at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V"))
  // public void setupTransformsMixin(LivingEntityRenderer<T, M> renderer, T
  // entity, MatrixStack matrices, float o,
  // float h, float g) {
  // T livingEntity = (T) entity;
  // float secondo;
  // float secondh;
  // float secondg;
  // if (livingEntity.isDead()) {
  // secondo = 0.0F;
  // secondh = 0.0F;
  // secondg = 0.0F;
  // } else {
  // secondo = o;
  // secondh = h;
  // secondg = g;
  // }
  // this.setupTransforms(livingEntity, matrices, secondo, secondh, secondg);
  // }

  // @Shadow
  // public void setupTransforms(T entity, MatrixStack matrices, float
  // animationProgress, float bodyYaw, float tickDelta) {
  // }

  // @Redirect(method =
  // "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
  // at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"))
  // public void setAnglesMixin(M model, Entity entity, float q, float p, float o,
  // float k, float m) {
  // T livingEntity = (T) entity;
  // float secondq;
  // float secondp;
  // float secondo;
  // float secondk;
  // float secondm;
  // if (livingEntity.deathTime > 0) {
  // secondq = 0.0F;
  // secondp = 0.0F;
  // secondo = 0.0F;
  // secondk = 0.0F;
  // secondm = 0.0F;
  // } else {
  // secondq = q;
  // secondp = p;
  // secondo = o;
  // secondk = k;
  // secondm = m;
  // }
  // this.model.setAngles(livingEntity, secondq, secondp, secondo, secondk,
  // secondm);
  // }

  // @Redirect(method =
  // "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
  // at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/client/render/entity/model/EntityModel;animateModel(Lnet/minecraft/entity/Entity;FFF)V"))
  // public void animateModelMixin(M model, Entity entity, float q, float p, float
  // g) {
  // T livingEntity = (T) entity;
  // float secondq;
  // float secondp;
  // float secondg;
  // if (livingEntity.isDead()) {
  // secondq = 0.0F;
  // secondp = 0.0F;
  // secondg = 0.0F;
  // } else {
  // secondq = q;
  // secondp = p;
  // secondg = g;
  // }
  // this.model.animateModel(livingEntity, secondq, secondp, secondg);
  // }

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

  // Test verification
  @Inject(method = "render", at = @At("HEAD"))
  private void lol(T livingEntity, float f, float g, MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
    if (livingEntity.deathTime > 0) {
      // System.out.println(g);
      // livingEntity.age = -1;
    }

  }

  // @Inject(method = "setupTransforms", at = @At("HEAD"))
  // public void setupTransformsMixin(T entity, MatrixStack matrices, float
  // animationProgress, float bodyYaw,
  // float tickDelta, CallbackInfo info) {
  // if (entity.deathTime > 0) {
  // // float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
  // // f = MathHelper.sqrt(f);
  // // if (f > 1.0F) {
  // // f = 1.0F;
  // // }

  // }
  // }
  // @Redirect(at = @At(value = "INVOKE",target =
  // "Lnet.minecraft.entity.LivingEntity;deathTime"),method = "setupTransforms")
  // @Redirect(at=@At(value="FIELD", target="signature of the deathTime field",
  // opcode=GETFIELD), method="setupTransforms")

  // @Overwrite
  // public void setupTransforms(T entity, MatrixStack matrices, float
  // animationProgress, float bodyYaw, float tickDelta) {
  // if (entity.deathTime > 0) {
  // matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(20F));
  // }
  // if (this.isShaking(entity) && entity.deathTime > 0) {
  // bodyYaw += (float) (Math.cos((double) entity.age * 3.25D) *
  // 3.141592653589793D * 0.4000000059604645D);
  // }

  // EntityPose entityPose = entity.getPose();
  // if (entityPose != EntityPose.SLEEPING) {
  // matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F -
  // bodyYaw));
  // }

  // if (entity.deathTime > 0) {
  // float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
  // f = MathHelper.sqrt(f);
  // if (f > 1.0F) {
  // f = 1.0F;
  // }

  // matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f *
  // this.getLyingAngle(entity)));

  // // entity.setBoundingBox(new Box());
  // // entity.setBoundingBox(new Box(f, f, f, f, f, f));
  // // this.getModel().animateModel(entity, 1F, 1F, 1F);
  // // this.getModel().setAngles(entity, 0F, 0F, 0F, 0F, 0F);
  // // animationProgress = 1F;
  // // entity.setMovementSpeed(0F);

  // // if (entity instanceof PathAwareEntity) {
  // // ((PathAwareEntity) entity).getNavigation().stop();
  // // }
  // // entity.setVelocity(Vec3d.ZERO);

  // if (entity instanceof CaveSpiderEntity) { // Okay
  // matrices.translate(0.0D, -0.55D, 0.0D);
  // } else if (entity instanceof SpiderEntity) { // Okay
  // matrices.translate(0.0D, -0.75D, 0.0D);
  // } else if (entity instanceof CatEntity) {
  // matrices.translate(-0.005D, 0.0D, 0.0D);
  // } else if (entity instanceof FishEntity) {
  // matrices.translate(-0.4D, 0.0D, 0.0D);
  // } else if (entity instanceof DolphinEntity) { // Okay
  // matrices.translate(-0.1D, 0.0D, 0.0D);
  // } else if (entity instanceof GuardianEntity) {
  // matrices.translate(-0.05D, 0.0D, 0.0D);
  // } else if (entity instanceof ElderGuardianEntity) {
  // matrices.translate(-0.05D, 0.0D, 0.0D);
  // } else if (entity instanceof EndermiteEntity) {
  // matrices.translate(0.1D, 0.0D, 0.0D);
  // } else {
  // matrices.translate(0.26D, 0.0D, 0.0D);
  // if (entity.isBaby()) {

  // }
  // }

  // } else if (entity.isUsingRiptide()) {
  // matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F -
  // entity.pitch));
  // matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(((float)
  // entity.age + tickDelta) * -75.0F));
  // } else if (entityPose == EntityPose.SLEEPING) {
  // Direction direction = entity.getSleepingDirection();
  // float g = direction != null ? getYaw(direction) : bodyYaw;
  // matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(g));
  // matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.getLyingAngle(entity)));
  // matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
  // } else if (entity.hasCustomName() || entity instanceof PlayerEntity) {
  // String string = Formatting.strip(entity.getName().getString());
  // if (("Dinnerbone".equals(string) || "Grumm".equals(string))
  // && (!(entity instanceof PlayerEntity) || ((PlayerEntity)
  // entity).isPartVisible(PlayerModelPart.CAPE))) {
  // matrices.translate(0.0D, (double) (entity.getHeight() + 0.1F), 0.0D);
  // matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
  // }
  // }

  // }

  // @Inject(method = "render", at = @At(value = "INVOKE", target =
  // "net/minecraft/client/render/entity/model/EntityModel.animateModel(Lnet/minecraft/entity/Entity;FFF)V"),
  // cancellable = true)
  // private void renderMixin(T livingEntity, float f, float g, MatrixStack
  // matrixStack,
  // VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
  // if (livingEntity instanceof LivingEntity) {
  // if (livingEntity.deathTime > 0) {

  // MinecraftClient minecraftClient = MinecraftClient.getInstance();
  // boolean bl = this.isVisible(livingEntity);
  // boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraftClient.player);
  // boolean bl3 = minecraftClient.method_27022(livingEntity);
  // RenderLayer renderLayer = this.getRenderLayer(livingEntity, bl, bl2, bl3);
  // if (renderLayer != null) {
  // VertexConsumer vertexConsumer =
  // vertexConsumerProvider.getBuffer(renderLayer);
  // int r = getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
  // this.model.render(matrixStack, vertexConsumer, i, r, 1.0F, 1.0F, 1.0F, bl2 ?
  // 0.15F : 1.0F);
  // }
  // matrixStack.pop();
  // super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
  // info.cancel();
  // }
  // }
  // }

  // @ModifyArg(method = "render", at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/client/render/entity/model/EntityModel.animateModel(Lnet/minecraft/entity/Entity;FFF)V"),
  // index = 1)
  // private float mixin(int in) {
  // if (in > 5) {
  // return in + 10;
  // } else {
  // return in - 10;
  // }
  // }

  // @Inject(method = "render", at = @At(value = "HEAD"))
  // private void renderMixin(T livingEntity, float f, float g, MatrixStack
  // matrixStack,
  // VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
  // // if (livingEntity instanceof LivingEntity) {
  // if (livingEntity.deathTime > 0) {
  // g = 0F;
  // System.out.print("x");
  // }
  // // }
  // }
  // @Inject(method = "render", at = @At(value = "TAIL"))
  // private void renderMixinx(T livingEntity, float f, float g, MatrixStack
  // matrixStack,
  // VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
  // // if (livingEntity instanceof LivingEntity) {
  // if (livingEntity.deathTime > 0) {
  // System.out.print(g);
  // }
  // // }
  // }

  // @Inject(method = "render", at = @At(value = "INVOKE", target =
  // "net/minecraft/client/render/entity/model/EntityModel.animateModel(Lnet/minecraft/entity/Entity;FFF)V"),
  // cancellable = true)
  // private void renderMixinlol(T livingEntity, float f, float g, MatrixStack
  // matrixStack,
  // VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
  // // if (livingEntity instanceof LivingEntity) {
  // if (livingEntity.deathTime > 0) {
  // float o = 0F;
  // }
  // // }
  // }

  // @Redirect(method = "render", at = @At(value = "INVOKE", target =
  // "net/minecraft/client/render/entity/model/EntityModel.animateModel(Lnet/minecraft/entity/Entity;FFF)V"))
  // private void renderMixin(T livingEntity, float f, float g, MatrixStack
  // matrixStack,
  // VertexConsumerProvider vertexConsumerProvider, int i) {
  // if (livingEntity instanceof LivingEntity) {
  // if (livingEntity.deathTime > 0) {
  // System.out.print("");
  // }
  // }
  // }

}
