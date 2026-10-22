package net.rpgz.mixin;

import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @ModifyExpressionValue(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesByClass(Ljava/lang/Class;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private List<HostileEntity> trySleepMixin(List<HostileEntity> list) {
        list.removeIf(LivingEntity::isDead);
        return list;
    }
}
