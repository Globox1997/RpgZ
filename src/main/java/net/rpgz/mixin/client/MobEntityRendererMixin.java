package net.rpgz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
@Mixin(MobEntityRenderer.class)
public abstract class MobEntityRendererMixin<T extends MobEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public MobEntityRendererMixin(Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    protected boolean isShaking(T entity) {
        if (entity.isDead()) {
            return false;
        }
        return super.isShaking(entity);
    }

    @Override
    protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
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
                // (double) -((entity.getHeight()) * f) * lyinganglebonus
                matrices.translate(-(double) ((entity.getHeight() / 2) * f), 0.0D, 0.0D);
            }
        }
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
    }

    @Override
    protected float getAnimationProgress(T entity, float tickDelta) {
        if (entity.isDead()) {
            return 0.0f;
        }
        return super.getAnimationProgress(entity, tickDelta);
    }

}
