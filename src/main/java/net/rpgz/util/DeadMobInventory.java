package net.rpgz.util;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;

public interface DeadMobInventory {

    public SimpleInventory getDeadMobInventory();

    default public void readDeadMobInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbt.contains("DeadMobInventory", NbtElement.LIST_TYPE)) {
            this.getDeadMobInventory().readNbtList(nbt.getList("DeadMobInventory", NbtElement.COMPOUND_TYPE), wrapperLookup);
        }
    }

    default public void writeDeadMobInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbt.put("DeadMobInventory", this.getDeadMobInventory().toNbtList(wrapperLookup));
    }
}
