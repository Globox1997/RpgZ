package net.rpgz.mixin;

import java.util.List;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.rpgz.util.RpgHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.rpgz.init.ConfigInit;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    private static int ticking = 0;

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputItemEntities(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void extractMixin(World world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (ConfigInit.CONFIG.hopper_extracting) {
            ticking++;
            if (ticking >= 20) {
                BlockPos pos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
                Box box = new Box(pos).expand(0.0D, 1.0D, 0.0D);
                List<MobEntity> list = world.getEntitiesByClass(MobEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR);
                if (!list.isEmpty()) {
                    for (MobEntity mobEntity : list) {
                        if (mobEntity.isDead()) {
                            Direction direction = Direction.DOWN;
                            for (int i : getAvailableSlots(RpgHelper.getDeadMobInventory(mobEntity), direction)) {
                                if (!extract(hopper, RpgHelper.getDeadMobInventory(mobEntity), i, direction)) {
                                    continue;
                                }
                                info.setReturnValue(true);
                            }
                        }
                    }
                }
                ticking = 0;
            }
        }
    }

    @Shadow
    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    private static int[] getAvailableSlots(Inventory inventory, Direction side) {
        return null;
    }
}
