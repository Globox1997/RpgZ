package net.rpgz.access;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public interface InventoryAccess {

    public SimpleInventory inventory = new SimpleInventory(9);

    public void addingInventoryItems(ItemStack stack);
}
