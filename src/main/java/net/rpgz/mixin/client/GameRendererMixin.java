package net.rpgz.mixin.client;

import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.rpgz.util.CorpseBox;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Corpses get targeted by their rotated CorpseBox below, not by their bigger axis aligned bounding box
    @ModifyArg(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"), index = 4)
    private Predicate<Entity> findCrosshairTargetPredicateMixin(Predicate<Entity> predicate) {
        return predicate.and(entity -> !CorpseBox.isCorpse(entity));
    }

    @Inject(method = "findCrosshairTarget", at = @At("RETURN"), cancellable = true)
    private void findCrosshairTargetMixin(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta, CallbackInfoReturnable<HitResult> info) {
        Vec3d start = camera.getCameraPosVec(tickDelta);
        Vec3d direction = camera.getRotationVec(tickDelta);
        Vec3d end = start.add(direction.multiply(entityInteractionRange));
        Box searchBox = camera.getBoundingBox().stretch(direction.multiply(entityInteractionRange)).expand(1.0D);

        MobEntity corpse = null;
        Vec3d corpseHitPos = null;
        double corpseDistance = Double.MAX_VALUE;
        for (MobEntity mobEntity : camera.getWorld().getEntitiesByClass(MobEntity.class, searchBox, entity -> entity != camera && CorpseBox.isCorpse(entity) && !entity.isSpectator() && entity.canHit())) {
            Optional<Vec3d> hitPos = CorpseBox.raycast(mobEntity, start, end);
            if (hitPos.isPresent() && start.squaredDistanceTo(hitPos.get()) < corpseDistance) {
                corpse = mobEntity;
                corpseHitPos = hitPos.get();
                corpseDistance = start.squaredDistanceTo(corpseHitPos);
            }
        }
        if (corpse == null) {
            return;
        }

        HitResult hitResult = info.getReturnValue();
        if (hitResult.getType() == HitResult.Type.MISS || corpseDistance < start.squaredDistanceTo(hitResult.getPos()) || this.isCorpseInsideTargetedBlock(camera, hitResult, corpse)) {
            info.setReturnValue(new EntityHitResult(corpse, corpseHitPos));
        }
    }

    private boolean isCorpseInsideTargetedBlock(Entity camera, HitResult hitResult, MobEntity corpse) {
        if (hitResult instanceof BlockHitResult blockHitResult && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockHitResult.getBlockPos();
            return !camera.getWorld().getBlockState(pos).isFullCube(camera.getWorld(), pos) && corpse.getBoundingBox().intersects(new Box(pos));
        }
        return false;
    }

}
