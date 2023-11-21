package net.rpgz.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.rpgz.access.InventoryAccess;
import net.spoiledz.util.SpoiledUtil;

public class RpgHelper {

    private static final boolean isSpoiledZLoaded = FabricLoader.getInstance().isModLoaded("spoiledz");

    public static void addStackToInventory(MobEntity mobEntity, ItemStack stack, World world) {
        if (!world.isClient() && !stack.isEmpty()) {
            if (isSpoiledZLoaded) {
                SpoiledUtil.setItemStackSpoilage(world, stack, null);
            }
            ((InventoryAccess) mobEntity).getInventory().addStack(stack);
        }
    }

}
