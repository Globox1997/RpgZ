package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "getPackedLight", at = @At("TAIL"), cancellable = true)
    public final void getLightMixin(T entity, float tickDelta, CallbackInfoReturnable<Integer> info) {
        if (entity.isLiving() && ((LivingEntity) entity).getShouldBeDead()) {
            AxisAlignedBB box = entity.getBoundingBox();
            BlockPos blockPos = new BlockPos(box.getCenter().getX(), box.maxY, box.getCenter().getZ());
            info.setReturnValue(LightTexture.packLight(this.getBlockLight(entity, blockPos),
                    this.getSkyLight(entity, blockPos)));
        }
    }

    @Shadow
    protected int getBlockLight(T entity, BlockPos blockPos) {
        return 0;
    }

    @Shadow
    protected int getSkyLight(T entity, BlockPos blockPos) {
        return 0;
    }

}