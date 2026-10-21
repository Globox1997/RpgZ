package net.rpgz.screen;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.rpgz.init.SoundInit;
import net.rpgz.init.TagInit;
import org.jetbrains.annotations.Nullable;

public class MobEntityScreenHandler extends ScreenHandler {
    private final SimpleInventory inventory;
    @Nullable
    private final MobEntity mobEntity;

    public MobEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(9), null);
    }

    public MobEntityScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory simpleInventory, @Nullable MobEntity mobEntity) {
        super(ScreenHandlerType.GENERIC_9X1, syncId);
        this.inventory = simpleInventory;
        this.mobEntity = mobEntity;

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
        return this.mobEntity == null || player.canInteractWithEntity(this.mobEntity, 4.0D);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        if (index >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack2 = slot.getStack();
        ItemStack itemStack = itemStack2.copy();
        if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
            return ItemStack.EMPTY;
        }
        if (itemStack2.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (itemStack.isIn(TagInit.RARE_ITEMS)) {
            player.playSound(SoundInit.COIN_LOOT_SOUND_EVENT, 1F, 1F);
        } else {
            player.playSound(SoundInit.LOOT_SOUND_EVENT, 1F, 1F);
        }

        return itemStack;
    }

}
