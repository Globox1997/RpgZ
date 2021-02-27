package net.rpgz.access;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface IInventoryAccess {

    public Inventory getDropsInventory();
    public void setDropsInventory(Inventory inventory);

    public void addingInventoryItems(ItemStack stack);
}