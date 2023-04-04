package io.github.foundationgames.automobility.screen;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SingleSlotScreenHandler extends AbstractContainerMenu {
    private final Container inventory;

    public SingleSlotScreenHandler(int syncId, Inventory playerInv) {
        this(syncId, playerInv, new SimpleContainer(1));
    }

    public SingleSlotScreenHandler(int syncId, Inventory playerInv, Container inv) {
        super(Automobility.SINGLE_SLOT_SCREEN.require("Single slot screen not registered!"), syncId);

        checkContainerSize(inv, 1);
        this.inventory = inv;
        inv.startOpen(playerInv.player);

        this.addSlot(new Slot(inventory, 0, 80, 23));

        // Player inventory
        int row;
        int col;
        for(row = 0; row < 3; ++row) {
            for(col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 58 + row * 18));
            }
        }
        for(row = 0; row < 9; ++row) {
            this.addSlot(new Slot(playerInv, row, 8 + row * 18, 116));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public ItemStack quickMoveStack(Player player, int fromSlotId) {
        var newStack = ItemStack.EMPTY;
        var fromSlot = this.slots.get(fromSlotId);

        if (fromSlot.hasItem()) {
            var fromStack = fromSlot.getItem();
            newStack = fromStack.copy();
            if (fromSlotId == 0) {
                if (!this.moveItemStackTo(fromStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(fromStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (fromStack.isEmpty()) {
                fromSlot.set(ItemStack.EMPTY);
            } else {
                fromSlot.setChanged();
            }
        }

        return newStack;
    }
}
