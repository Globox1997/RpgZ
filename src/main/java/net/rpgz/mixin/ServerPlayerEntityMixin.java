package net.rpgz.mixin;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Either;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "trySleep", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void trySleepMixin(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> info, Direction direction, double d, double e, Vec3d vec3d,
            List<HostileEntity> list) {
        if (!list.isEmpty()) {
            List<HostileEntity> removeList = new ArrayList<HostileEntity>();
            for (int o = 0; o < list.size(); ++o) {
                HostileEntity entityFromList = (HostileEntity) list.get(o);
                if (entityFromList.isDead()) {
                    removeList.add(entityFromList);
                }
            }
            list.removeAll(removeList);
        }
    }
}
