package net.rpgz.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.rpgz.init.SoundInit;
import net.rpgz.init.TagInit;

public class MobEntityScreenHandler extends ScreenHandler {
    private final SimpleInventory inventory;

    public MobEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory());
    }

    public MobEntityScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory simpleInventory) {
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
    public ItemStack quickMove(PlayerEntity player, int index) {
        Boolean rareItem = false;
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (itemStack.isIn(TagInit.RARE_ITEMS)) {
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
            player.playSound(SoundInit.COIN_LOOT_SOUND_EVENT, SoundCategory.PLAYERS, 1F, 1F);
        } else {
            player.playSound(SoundInit.LOOT_SOUND_EVENT, SoundCategory.PLAYERS, 1F, 1F);
        }

        return itemStack;
    }

}
