package net.rpgz.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface DeadMobInventory {

    public SimpleContainer getDeadMobInventory();

    default public void readDeadMobInventory(ValueInput input) {
        input.list("DeadMobInventory", ItemStack.CODEC).ifPresent(list -> this.getDeadMobInventory().fromItemList(list));
    }

    default public void writeDeadMobInventory(ValueOutput output) {
        this.getDeadMobInventory().storeAsItemList(output.list("DeadMobInventory", ItemStack.CODEC));
    }
}
