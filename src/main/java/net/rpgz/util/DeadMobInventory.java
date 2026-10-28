package net.rpgz.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;

public interface DeadMobInventory {

    public SimpleContainer getDeadMobInventory();

    default public void readDeadMobInventory(CompoundTag nbt, HolderLookup.Provider wrapperLookup) {
        if (nbt.contains("DeadMobInventory", Tag.TAG_LIST)) {
            this.getDeadMobInventory().fromTag(nbt.getList("DeadMobInventory", Tag.TAG_COMPOUND), wrapperLookup);
        }
    }

    default public void writeDeadMobInventory(CompoundTag nbt, HolderLookup.Provider wrapperLookup) {
        nbt.put("DeadMobInventory", this.getDeadMobInventory().createTag(wrapperLookup));
    }
}
