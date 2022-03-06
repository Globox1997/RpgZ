package net.rpgz.mixin;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.render.entity.EntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    @Shadow
    protected M model;
    @Shadow
    protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();

    public LivingEntityRendererMixin(EntityRendererFactory.Context ctx, M model) {
        super(ctx);
        this.model = model;
    }

    @Inject(method = "getOverlay", at = @At("HEAD"), cancellable = true)
    private static void getOverlayMixin(LivingEntity entity, float whiteOverlayProgress, CallbackInfoReturnable<Integer> info) {
        if (entity instanceof MobEntity)
            info.setReturnValue(OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0)));
    }

    // Check for block next to the mob before turning to the side
    // @Redirect(method = "setupTransforms", at = @At(value = "INVOKE",target = "Lnet/minecraft/util/math/MathHelper;sqrt(F)F"))
    // public float testMixin(float f,LivingEntity livingEntity, MatrixStack matrices, float a, float b, float c) {
    // // b maybe winkel
    // if(livingEntity.deathTime == 1){
    // Box box = livingEntity.getBoundingBox();
    // box.expand(livingEntity.getRotationVecClient().x, livingEntity.getRotationVecClient().y, livingEntity.getRotationVecClient().z);
    // // box.
    // BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).up();
    // BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
    // System.out.println(livingEntity.getRotationVecClient().z+"::"+box.getZLength()+"::"+livingEntity.world.getBlockState(blockPos)+"::"+livingEntity.world.getBlockState(blockPos2)+"::"+blockPos);
    // // this.setBoundingBox(newBoundingBox.offset(this.getRotationVector(0F, this.bodyYaw).rotateY(-30.0F)));
    // }
    // //System.out.println(f+":"+a+":"+b+":"+c);

    // // world.getBlockState(blockPos).isFullCube(world, blockPos)
    // // if(){

    // // }
    // return MathHelper.sqrt(f);
    // }
    // FLnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF

    // net/minecraft/client/util/math/MatrixStack.multiply (Lnet/minecraft/util/math/Quaternion;)V
    // @Redirect(method = "setupTransforms", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V"))
    // public void testMixin(MatrixStack matrices, Quaternion quaternion) {

    // float f = ((float)entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
    // f = MathHelper.sqrt(f);
    // if (f > 1.0F) {
    // f = 1.0F;
    // }
    // matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * this.getLyingAngle(entity)));
    // }
    // Lnet/minecraft/util/math/MathHelper;sqrt(F)F
    // Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V

    // @ModifyVariable(method = "setupTransforms", at = @At(value = "INVOKE",target = "Lnet/minecraft/util/math/MathHelper;sqrt(F)F",shift = Shift.AFTER),ordinal = 3) //ordinal = 3
    // public float testMixin(float original) {
    // // System.out.println(original);
    // return 3.0F;
    // }

    @Inject(method = "setupTransforms", at = @At("HEAD"))
    public void setupTransformsMixin(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo info) {
        if (entity instanceof MobEntity && entity.deathTime > 0) {
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
                // (double) -((entity.getHeight()) * f) * lyinganglebonus
                matrices.translate(-(double) ((entity.getHeight() / 2) * f), 0.0D, 0.0D);
            }
        }
    }

    @Redirect(method = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;isShaking(Lnet/minecraft/entity/LivingEntity;)Z"))
    public boolean isShakingMixin(LivingEntityRenderer<T, M> renderer, T entity, T secondentity, MatrixStack matrix, float o, float k, float m) {
        if (entity instanceof MobEntity)
            if (!entity.isDead() && this.isShaking(entity))
                return true;
            else
                return false;
        else
            return false;
    }

    @Inject(method = "getAnimationProgress", at = @At("HEAD"), cancellable = true)
    public void getAnimationProgress(T entity, float tickDelta, CallbackInfoReturnable<Float> info) {
        if (entity instanceof MobEntity && entity.isDead())
            info.setReturnValue(0.0F);
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
