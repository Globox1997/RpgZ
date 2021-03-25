package net.rpgz.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.rpgz.access.IInventoryAccess;
import net.rpgz.forge.config.RPGZConfig;

@Mixin(HopperTileEntity.class)
public abstract class HopperBlockEntityMixin {
    private static int ticking = 0;

    @Inject(method = "Lnet/minecraft/tileentity/HopperTileEntity;pullItems(Lnet/minecraft/tileentity/IHopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/HopperTileEntity;getCaptureItems(Lnet/minecraft/tileentity/IHopper;)Ljava/util/List;"), cancellable = true)
    private static void extractMixin(IHopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (RPGZConfig.hopper_extracting.get()) {
            ticking++;
            if (ticking >= 20) {
                BlockPos pos = new BlockPos(hopper.getXPos(), hopper.getYPos(), hopper.getZPos());
                AxisAlignedBB box = new AxisAlignedBB(pos).expand(0.0D, 1.0D, 0.0D);
                System.out.println(box);
                List<LivingEntity> list = hopper.getWorld().getEntitiesWithinAABB(LivingEntity.class, box,
                        EntityPredicates.NOT_SPECTATING);
                if (!list.isEmpty()) {
                    Iterator<LivingEntity> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        LivingEntity livingEntity = (LivingEntity) iterator.next();
                        if (livingEntity.getShouldBeDead()) {
                            if (((IInventoryAccess) livingEntity).getDropsInventory() != null) {
                                Direction direction = Direction.DOWN;
                                info.setReturnValue(
                                        isInventoryEmpty(((IInventoryAccess) livingEntity).getDropsInventory(), direction) ? false
                                                : func_213972_a(((IInventoryAccess) livingEntity).getDropsInventory(),
                                                        direction).anyMatch((i) -> {
                                                            return pullItemFromSlot(hopper,
                                                                    ((IInventoryAccess) livingEntity).getDropsInventory(), i,
                                                                    direction);
                                                        }));
                            }
                        }
                    }
                }
                ticking = 0;
            }
        }
    }

    @Shadow
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    private static boolean isInventoryEmpty(IInventory inv, Direction facing) {
        return false;
    }

    @Shadow
    private static IntStream /*getAvailableSlots*/func_213972_a(IInventory inventory, Direction side) {
        return null;
    }
}