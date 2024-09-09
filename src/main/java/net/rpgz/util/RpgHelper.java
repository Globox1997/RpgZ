package net.rpgz.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;

public class RpgHelper {

    private static final boolean isSpoiledZLoaded = FabricLoader.getInstance().isModLoaded("spoiledz");

    public static SimpleInventory getDeadMobInventory(MobEntity mobEntity) {
        return ((DeadMobInventory) mobEntity).getDeadMobInventory();
    }

}
