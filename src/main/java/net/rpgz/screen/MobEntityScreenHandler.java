package net.rpgz.screen;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.rpgz.init.SoundInit;
import net.rpgz.init.TagInit;
import org.jetbrains.annotations.Nullable;

public class MobEntityScreenHandler extends AbstractContainerMenu {
    private final SimpleContainer inventory;
    @Nullable
    private final Mob mobEntity;

    public MobEntityScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(9), null);
    }

    public MobEntityScreenHandler(int syncId, Inventory playerInventory, SimpleContainer simpleInventory, @Nullable Mob mobEntity) {
        super(MenuType.GENERIC_9x1, syncId);
        this.inventory = simpleInventory;
        this.mobEntity = mobEntity;

        int m;
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(inventory, m, 8 + m * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
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
    public boolean stillValid(Player player) {
        return this.mobEntity == null || player.isWithinEntityInteractionRange(this.mobEntity, 4.0D);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index >= this.inventory.getContainerSize()) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack2 = slot.getItem();
        ItemStack itemStack = itemStack2.copy();
        if (!this.moveItemStackTo(itemStack2, this.inventory.getContainerSize(), this.slots.size(), true)) {
            return ItemStack.EMPTY;
        }
        if (itemStack2.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (itemStack.is(TagInit.RARE_ITEMS)) {
            player.playSound(SoundInit.COIN_LOOT_SOUND_EVENT, 1F, 1F);
        } else {
            player.playSound(SoundInit.LOOT_SOUND_EVENT, 1F, 1F);
        }

        return itemStack;
    }

}
