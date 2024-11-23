package net.rpgz.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Redirect(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
	@Inject(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void isPreventingPlayerRestAndIsAlive(BlockPos pos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> info, Direction direction, double d, double e, Vec3 vec3d, List<Monster> list) {
		if (!list.isEmpty()) {
            List<Monster> removeList = new ArrayList<Monster>();
            for (int o = 0; o < list.size(); ++o) {
            	Monster entityFromList = (Monster) list.get(o);
                if (entityFromList.isDeadOrDying()) {
                    removeList.add(entityFromList);
                }
            }
            list.removeAll(removeList);
        }
	}
}
