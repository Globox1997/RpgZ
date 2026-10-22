package net.rpgz.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.TagInit;

public class RpgHelper {

    public static SimpleInventory getDeadMobInventory(MobEntity mobEntity) {
        return ((DeadMobInventory) mobEntity).getDeadMobInventory();
    }

    public static boolean isExcludedFromLooting(MobEntity mobEntity) {
        return mobEntity.getType().isIn(TagInit.EXCLUDED_ENTITIES) || ConfigInit.EXCLUDED_ENTITIES.contains(EntityType.getId(mobEntity.getType()));
    }

    public static boolean shouldDropLootDirectly(MobEntity mobEntity) {
        return mobEntity.isBaby() || (ConfigInit.CONFIG.drop_unlooted && mobEntity.deathTime > ConfigInit.CONFIG.drop_after_ticks) || isExcludedFromLooting(mobEntity);
    }

}
