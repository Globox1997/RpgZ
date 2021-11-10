package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "getPackedLightCoords", at = @At("TAIL"), cancellable = true)
    public final void getLightMixin(T entity, float tickDelta, CallbackInfoReturnable<Integer> info) {
        if (entity.showVehicleHealth() && ((LivingEntity) entity).isDeadOrDying()) {
            AABB box = entity.getBoundingBox();
            BlockPos blockPos = new BlockPos(box.getCenter().x(), box.maxY, box.getCenter().z());
            info.setReturnValue(LightTexture.pack(this.getBlockLightLevel(entity, blockPos),
                    this.getSkyLightLevel(entity, blockPos)));
        }
    }

    @Shadow
    protected int getBlockLightLevel(T entity, BlockPos blockPos) {
        return 0;
    }

    @Shadow
    protected int getSkyLightLevel(T entity, BlockPos blockPos) {
        return 0;
    }

}