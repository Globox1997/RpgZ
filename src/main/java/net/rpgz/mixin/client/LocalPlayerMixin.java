package net.rpgz.mixin.client;

import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.rpgz.util.CorpseBox;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @ModifyArg(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"), index = 4)
    private static Predicate<Entity> findCrosshairTargetPredicateMixin(Predicate<Entity> predicate) {
        return predicate.and(entity -> !CorpseBox.isCorpse(entity));
    }

    @Inject(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At("RETURN"), cancellable = true)
    private static void findCrosshairTargetMixin(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta, CallbackInfoReturnable<HitResult> info) {
        Vec3 start = camera.getEyePosition(tickDelta);
        Vec3 direction = camera.getViewVector(tickDelta);
        Vec3 end = start.add(direction.scale(entityInteractionRange));
        AABB searchBox = camera.getBoundingBox().expandTowards(direction.scale(entityInteractionRange)).inflate(1.0D);

        Mob corpse = null;
        Vec3 corpseHitPos = null;
        double corpseDistance = Double.MAX_VALUE;
        for (Mob mobEntity : camera.level().getEntitiesOfClass(Mob.class, searchBox, entity -> entity != camera && CorpseBox.isCorpse(entity) && !entity.isSpectator() && entity.isPickable())) {
            Optional<Vec3> hitPos = CorpseBox.raycast(mobEntity, start, end);
            if (hitPos.isPresent() && start.distanceToSqr(hitPos.get()) < corpseDistance) {
                corpse = mobEntity;
                corpseHitPos = hitPos.get();
                corpseDistance = start.distanceToSqr(corpseHitPos);
            }
        }
        if (corpse == null) {
            return;
        }

        HitResult hitResult = info.getReturnValue();
        if (hitResult.getType() == HitResult.Type.MISS || corpseDistance < start.distanceToSqr(hitResult.getLocation()) || isCorpseInsideTargetedBlock(camera, hitResult, corpse)) {
            info.setReturnValue(new EntityHitResult(corpse, corpseHitPos));
        }
    }

    private static boolean isCorpseInsideTargetedBlock(Entity camera, HitResult hitResult, Mob corpse) {
        if (hitResult instanceof BlockHitResult blockHitResult && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockHitResult.getBlockPos();
            return !camera.level().getBlockState(pos).isCollisionShapeFullBlock(camera.level(), pos) && corpse.getBoundingBox().intersects(new AABB(pos));
        }
        return false;
    }

}
