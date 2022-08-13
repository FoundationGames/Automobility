package io.github.foundationgames.automobility.screen;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SingleSlotScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public SingleSlotScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(1));
    }

    public SingleSlotScreenHandler(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(Automobility.SINGLE_SLOT_SCREEN, syncId);

        checkSize(inv, 1);
        this.inventory = inv;
        inv.onOpen(playerInv.player);

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
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public ItemStack transferSlot(PlayerEntity player, int fromSlotId) {
        var newStack = ItemStack.EMPTY;
        var fromSlot = this.slots.get(fromSlotId);

        if (fromSlot.hasStack()) {
            var fromStack = fromSlot.getStack();
            newStack = fromStack.copy();
            if (fromSlotId == 0) {
                if (!this.insertItem(fromStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(fromStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (fromStack.isEmpty()) {
                fromSlot.setStack(ItemStack.EMPTY);
            } else {
                fromSlot.markDirty();
            }
        }

        return newStack;
    }
}
