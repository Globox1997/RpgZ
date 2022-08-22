package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Mutable
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void updateTargetedEntityMixin(float tickDelta, CallbackInfo info) {
        Entity entity = this.client.getCameraEntity();
        if (this.client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) this.client.crosshairTarget).getBlockPos();
            if (!this.client.world.getBlockState(pos).isFullCube(this.client.world, pos)) {
                double reachDinstance = (double) this.client.interactionManager.getReachDistance();
                Vec3d vec3d = this.client.player.getCameraPosVec(tickDelta);
                Vec3d vec3d2 = this.client.player.getRotationVec(tickDelta);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * reachDinstance, vec3d2.y * reachDinstance, vec3d2.z * reachDinstance);
                Box box = entity.getBoundingBox().stretch(vec3d2.multiply(reachDinstance)).expand(1.0D, 1.0D, 1.0D);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> {
                    return !entityx.isSpectator() && entityx.canHit();
                }, 5D);
                if (entityHitResult != null)
                    this.client.crosshairTarget = entityHitResult;
            }
        }
    }

}