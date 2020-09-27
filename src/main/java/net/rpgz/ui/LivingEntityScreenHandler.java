package net.rpgz.ui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.rpgz.sound.LootSounds;
import net.rpgz.tag.Tags;

import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;

public class LivingEntityScreenHandler extends ScreenHandler {
   private final SimpleInventory inventory;

   public LivingEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
      this(syncId, playerInventory, new SimpleInventory());
   }

   public LivingEntityScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory simpleInventory) {
      super(ScreenHandlerType.GENERIC_9X1, syncId);
      this.inventory = simpleInventory;

      int m;
      for (m = 0; m < 9; ++m) {
         this.addSlot(new Slot(inventory, m, 8 + m * 18, 20) {
            @Override
            public boolean canInsert(ItemStack stack) {
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
   public boolean canUse(PlayerEntity player) {
      return true;
   }

   @Override
   public ItemStack transferSlot(PlayerEntity player, int index) {
      Boolean rareItem = false;
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot) this.slots.get(index);
      if (slot != null && slot.hasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (itemStack.getItem().isIn(Tags.RARE_ITEMS)) {
            rareItem = true;
         }
         if (index < this.inventory.size()) {
            if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
            return ItemStack.EMPTY;
         }
         if (itemStack2.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
         } else {
            slot.markDirty();
         }
      }

      if (rareItem) {
         player.playSound(LootSounds.COIN_LOOT_SOUND_EVENT, SoundCategory.PLAYERS, 1F, 1F);
      } else {
         player.playSound(LootSounds.LOOT_SOUND_EVENT, SoundCategory.PLAYERS, 1F, 1F);
      }

      return itemStack;
   }

   @Override
   public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
      return false;
   }

   @Override
   public boolean canInsertIntoSlot(Slot slot) {
      return false;
   }

   @Override
   public void close(PlayerEntity player) {
      super.close(player);
      this.inventory.onClose(player);
   }

}
