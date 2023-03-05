package net.rpgz.mixin;

import java.util.List;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Redirect(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
	public List<Monster> isPreventingPlayerRestAndIsAlive(Level level, Class<Monster> pClazz, AABB pArea, Predicate<Monster> pFilter) {
		return level.getEntitiesOfClass(pClazz, pArea, (p_9062_) -> {
            return !p_9062_.isDeadOrDying() && p_9062_.isPreventingPlayerRest((ServerPlayer)(Object)this);
         });
	}
}
