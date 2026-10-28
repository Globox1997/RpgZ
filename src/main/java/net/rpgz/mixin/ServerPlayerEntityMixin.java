package net.rpgz.mixin;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {

    @ModifyExpressionValue(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private List<Monster> trySleepMixin(List<Monster> list) {
        list.removeIf(LivingEntity::isDeadOrDying);
        return list;
    }
}
