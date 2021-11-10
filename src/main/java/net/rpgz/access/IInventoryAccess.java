package net.rpgz.access;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public interface IInventoryAccess {

    public SimpleContainer getDropsInventory();
    public void setDropsInventory(SimpleContainer inventory);

    public void addingInventoryItems(ItemStack stack);
}