package net.rpgz.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.rpgz.init.ConfigInit;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputItemEntities(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void extractMixin(World world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (ConfigInit.CONFIG.hopper_extracting) {
            Box box = hopper.getInputAreaShape().offset(hopper.getHopperX() - 0.5D, hopper.getHopperY() - 0.5D, hopper.getHopperZ() - 0.5D);
            for (MobEntity mobEntity : world.getEntitiesByClass(MobEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR)) {
                if (mobEntity.isDead()) {
                    SimpleInventory deadMobInventory = RpgHelper.getDeadMobInventory(mobEntity);
                    for (int i : getAvailableSlots(deadMobInventory, Direction.DOWN)) {
                        if (extract(hopper, deadMobInventory, i, Direction.DOWN)) {
                            info.setReturnValue(true);
                            return;
                        }
                    }
                }
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
