package net.rpgz.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.TagInit;

public class RpgHelper {

    public static SimpleContainer getDeadMobInventory(Mob mobEntity) {
        return ((DeadMobInventory) mobEntity).getDeadMobInventory();
    }

    public static boolean isExcludedFromLooting(Mob mobEntity) {
        return mobEntity.getType().builtInRegistryHolder().is(TagInit.EXCLUDED_ENTITIES) || ConfigInit.EXCLUDED_ENTITIES.contains(EntityType.getKey(mobEntity.getType()));
    }

    public static boolean shouldDropLootDirectly(Mob mobEntity) {
        return mobEntity.isBaby() || (ConfigInit.CONFIG.drop_unlooted && mobEntity.deathTime > ConfigInit.CONFIG.drop_after_ticks) || isExcludedFromLooting(mobEntity);
    }

}
