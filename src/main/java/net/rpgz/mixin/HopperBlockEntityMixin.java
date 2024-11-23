package net.rpgz.mixin;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.rpgz.access.IInventoryAccess;
import net.rpgz.init.ConfigInit;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    private static int ticking = 0;

    @Inject(method = "suckInItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getItemsAtAndAbove(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void extractMixin(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (ConfigInit.CONFIG.hopper_extracting) {
            ticking++;
            if (ticking >= 20) {
                BlockPos pos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ());
                AABB box = new AABB(pos).expandTowards(0.0D, 1.0D, 0.0D);
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box,
                        EntitySelector.NO_SPECTATORS);
                if (!list.isEmpty()) {
                    Iterator<LivingEntity> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        LivingEntity livingEntity = (LivingEntity) iterator.next();
                        if (livingEntity.isDeadOrDying()) {
                            if (((IInventoryAccess) livingEntity).getDropsInventory() != null) {
                                Direction direction = Direction.DOWN;

                                for (int i : getSlots(((IInventoryAccess) livingEntity).getDropsInventory(), direction)) {
                                    if (!tryTakeInItemFromSlot(hopper, ((IInventoryAccess) livingEntity).getDropsInventory(), i, direction)) {
                                        continue;
                                    }
                                    info.setReturnValue(true);
                                }
                            }
                        }
                    }
                }
                ticking = 0;
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