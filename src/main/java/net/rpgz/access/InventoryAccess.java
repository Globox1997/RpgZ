package net.rpgz.access;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public interface InventoryAccess {

    public SimpleInventory getInventory();

    public void addingInventoryItems(ItemStack stack);
}
