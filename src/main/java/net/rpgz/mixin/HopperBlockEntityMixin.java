package net.rpgz.mixin;

import net.rpgz.util.RpgHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.rpgz.init.ConfigInit;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "suckInItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getItemsAtAndAbove(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void extractMixin(Level world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (ConfigInit.CONFIG.hopper_extracting) {
            AABB box = hopper.getSuckAabb().move(hopper.getLevelX() - 0.5D, hopper.getLevelY() - 0.5D, hopper.getLevelZ() - 0.5D);
            for (Mob mobEntity : world.getEntitiesOfClass(Mob.class, box, EntitySelector.NO_SPECTATORS)) {
                if (mobEntity.isDeadOrDying()) {
                    SimpleContainer deadMobInventory = RpgHelper.getDeadMobInventory(mobEntity);
                    for (int i : getSlots(deadMobInventory, Direction.DOWN)) {
                        if (tryTakeInItemFromSlot(hopper, deadMobInventory, i, Direction.DOWN)) {
                            info.setReturnValue(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Shadow
    private static boolean tryTakeInItemFromSlot(Hopper hopper, Container inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    private static int[] getSlots(Container inventory, Direction side) {
        return null;
    }
}
