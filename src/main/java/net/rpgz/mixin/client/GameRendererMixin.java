package net.rpgz.mixin.client;

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
import net.minecraft.entity.mob.MobEntity;
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

    @Inject(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void updateCrosshairTargetMixin(float tickDelta, CallbackInfo info) {
        if (this.client.crosshairTarget != null && this.client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) this.client.crosshairTarget).getBlockPos();
            if (!this.client.world.getBlockState(pos).isFullCube(this.client.world, pos)) {
                Entity camera = this.client.getCameraEntity();
                double reachDistance = this.client.player.getEntityInteractionRange();
                Vec3d vec3d = camera.getCameraPosVec(tickDelta);
                Vec3d vec3d2 = camera.getRotationVec(tickDelta);
                Vec3d vec3d3 = vec3d.add(vec3d2.multiply(reachDistance));
                Box box = camera.getBoundingBox().stretch(vec3d2.multiply(reachDistance)).expand(1.0D, 1.0D, 1.0D);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, (entityx) -> {
                    return entityx instanceof MobEntity mobEntity && mobEntity.isDead() && !entityx.isSpectator() && entityx.canHit();
                }, reachDistance * reachDistance);
                if (entityHitResult != null && (vec3d.squaredDistanceTo(entityHitResult.getPos()) < vec3d.squaredDistanceTo(this.client.crosshairTarget.getPos())
                        || entityHitResult.getEntity().getBoundingBox().intersects(new Box(pos)))) {
                    this.client.crosshairTarget = entityHitResult;
                    this.client.targetedEntity = entityHitResult.getEntity();
                }
            }
        }
    }

}
