package net.rpgz.ui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.rpgz.sound.LootSounds;
import net.rpgz.tag.Tags;

public class LivingEntityScreenHandler extends Container {
   private final Inventory inventory;

   public LivingEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
      this(syncId, playerInventory, new Inventory());
   }

   public LivingEntityScreenHandler(int syncId, PlayerInventory playerInventory, Inventory simpleInventory) {
      super(ContainerType.GENERIC_9X1, syncId);
      this.inventory = simpleInventory;

      int m;
      for (m = 0; m < 9; ++m) {
         this.addSlot(new Slot(inventory, m, 8 + m * 18, 20) {
            @Override
            public boolean isItemValid(ItemStack stack) {
               return false;
            }
         });
      }
      for (m = 0; m < 3; ++m) {
         for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, m * 18 + 51));
         }
      }
      for (m = 0; m < 9; ++m) {
         this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 109));
      }

   }

   @Override
   public boolean canInteractWith(PlayerEntity player) {
      return true;
   }

   @Override
   public ItemStack transferStackInSlot(PlayerEntity player, int index) {
      Boolean rareItem = false;
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot) this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (itemStack.getItem().isIn(Tags.RARE_ITEMS)) {
            rareItem = true;
         }
         if (index < this.inventory.getSizeInventory()) {
            if (!this.mergeItemStack(itemStack2, this.inventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemStack2, 0, this.inventory.getSizeInventory(), false)) {
            return ItemStack.EMPTY;
         }
         if (itemStack2.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      if (rareItem) {
         player.playSound(LootSounds.COIN_LOOT_SOUND_EVENT, SoundCategory.PLAYERS, 1F, 1F);
      } else {
         player.playSound(LootSounds.LOOT_SOUND_EVENT, SoundCategory.PLAYERS, 1F, 1F);
      }

      return itemStack;
   }

}
