package net.rpgz.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ Monster.class, ZombifiedPiglin.class })
public class MonsterMixin {

    @Inject(method = "isPreventingPlayerRest", at = @At("HEAD"), cancellable = true)
    private void isPreventingPlayerRestMixin(ServerLevel level, Player player, CallbackInfoReturnable<Boolean> info) {
        if (((LivingEntity) (Object) this).isDeadOrDying()) {
            info.setReturnValue(false);
        }
    }
}
