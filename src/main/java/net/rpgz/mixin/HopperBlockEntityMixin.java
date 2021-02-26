package net.rpgz.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.rpgz.access.InventoryAccess;
import net.rpgz.config.Config;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin implements InventoryAccess {
    private static int ticking = 0;

    @Inject(method = "Lnet/minecraft/block/entity/HopperBlockEntity;extract(Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputItemEntities(Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void extractMixin(Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (Config.CONFIG.hopper_extracting) {
            ticking++;
            if (ticking >= 20) {
                BlockPos pos = new BlockPos(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
                Box box = new Box(pos).expand(0.0D, 1.0D, 0.0D);
                System.out.println(box);
                List<LivingEntity> list = hopper.getWorld().getEntitiesByClass(LivingEntity.class, box,
                        EntityPredicates.EXCEPT_SPECTATOR);
                if (!list.isEmpty()) {
                    Iterator<LivingEntity> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        LivingEntity livingEntity = (LivingEntity) iterator.next();
                        if (livingEntity.isDead()) {
                            System.out.print(((InventoryAccess) livingEntity).getInventory()+" ");
                            if (((InventoryAccess) livingEntity).getInventory() != null) {
                                Direction direction = Direction.DOWN;
                                info.setReturnValue(
                                        isInventoryEmpty(((InventoryAccess) livingEntity).getInventory(), direction)
                                                ? false
                                                : getAvailableSlots(((InventoryAccess) livingEntity).getInventory(),
                                                        direction).anyMatch((i) -> {
                                                            return extract(hopper,
                                                                    ((InventoryAccess) livingEntity).getInventory(), i,
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
    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return false;
    }

    @Shadow
    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        return null;
    }
}
