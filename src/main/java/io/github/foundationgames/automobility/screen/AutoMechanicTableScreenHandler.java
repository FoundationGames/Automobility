package io.github.foundationgames.automobility.screen;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AutoMechanicTableScreenHandler extends ScreenHandler {
    private final World world;
    private final ScreenHandlerContext context;
    private final Property selectedRecipe = Property.create();

    public List<AutoMechanicTableRecipe> recipes;

    public final List<Ingredient> missingIngredients = new ArrayList<>();
    public final SimpleInventory inputInv;
    public final Slot outputSlot;

    private final int playerInvSlot;

    public AutoMechanicTableScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, ScreenHandlerContext.EMPTY);
    }

    public AutoMechanicTableScreenHandler(int syncId, PlayerInventory playerInv, ScreenHandlerContext ctx) {
        super(Automobility.AUTO_MECHANIC_SCREEN, syncId);
        this.world = playerInv.player.getWorld();
        this.context = ctx;
        this.inputInv = new SimpleInventory(9) {
            @Override public void markDirty() { AutoMechanicTableScreenHandler.this.onInputUpdated(); }
        };

        for(int s = 0; s < 9; s++) {
            this.addSlot(new InputSlot(this.inputInv, s, 8 + (s * 18), 88));
        }
        this.outputSlot = this.addSlot(new OutputSlot(new SimpleInventory(1), 0, 26, 38));

        this.playerInvSlot = this.slots.size();
        int playerInvY = 127;
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + (row * 9) + 9, 8 + (col * 18), playerInvY + (row * 18)));
            }
        }
        for(int s = 0; s < 9; s++) {
            this.addSlot(new Slot(playerInv, s, 8 + (s * 18), playerInvY + 58));
        }

        this.recipes = new ArrayList<>(world.getRecipeManager().listAllOfType(AutoMechanicTableRecipe.TYPE));
        Collections.sort(this.recipes);

        this.selectedRecipe.set(-1);
        this.addProperty(this.selectedRecipe);
    }

    public Optional<AutoMechanicTableRecipe> getSelectedRecipe() {
        int id = this.selectedRecipe.get();
        return (id >= 0 && this.recipes.size() > 0 && id < this.recipes.size()) ? Optional.of(this.recipes.get(id)) : Optional.empty();
    }

    public int getSelectedRecipeId() {
        return this.selectedRecipe.get();
    }

    private void updateMissingIngredients() {
        this.missingIngredients.clear();

        this.getSelectedRecipe().ifPresent(recipe -> recipe.forMissingIngredients(this.inputInv, this.missingIngredients::add));
    }

    private void updateRecipeState() {
        this.updateMissingIngredients();

        this.getSelectedRecipe().ifPresent(recipe -> {
            if (recipe.matches(this.inputInv, this.world)) {
                this.outputSlot.setStack(recipe.getOutput().copy());
            } else {
                this.outputSlot.setStack(ItemStack.EMPTY);
            }
        });
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.recipes.size()) {
            this.selectRecipe(id);
            return true;
        }

        return super.onButtonClick(player, id);
    }

    private void onInputUpdated() {
        updateRecipeState();
    }

    private void selectRecipe(int id) {
        this.selectedRecipe.set(id);
        updateRecipeState();
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);

        this.outputSlot.setStack(ItemStack.EMPTY);
        this.context.run((world, pos) -> this.dropInventory(player, this.inputInv));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, AutomobilityBlocks.AUTO_MECHANIC_TABLE);
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.outputSlot && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int fromSlotId) {
        var newStack = ItemStack.EMPTY;
        var fromSlot = this.slots.get(fromSlotId);

        if (fromSlot.hasStack()) {
            var fromStack = fromSlot.getStack();
            var fromItem = fromStack.getItem();
            newStack = fromStack.copy();

            // Items transferred out of output slot
            if (fromSlotId == this.outputSlot.id) {
                fromItem.onCraft(fromStack, player.world, player);
                if (!this.insertItem(fromStack, this.playerInvSlot, this.playerInvSlot + 36, true)) {
                    return ItemStack.EMPTY;
                }

                fromSlot.onQuickTransfer(fromStack, newStack);
            // Items transferred out of input row
            } else if (this.slots.stream().anyMatch(s -> s.inventory == this.inputInv && s.id == fromSlotId)) {
                if (!this.insertItem(fromStack, this.playerInvSlot, this.playerInvSlot + 36, false)) {
                    return ItemStack.EMPTY;
                }
            // Items being transferred into the input row, which match the missing ingredients
            } else if (this.missingIngredients.stream().anyMatch(ing -> ing.test(fromStack))) {
                if (!this.insertItem(fromStack, 0, 8, false)) {
                    return ItemStack.EMPTY;
                }
            // Items transferred from inventory to hotbar
            } else if (fromSlotId >= this.playerInvSlot && fromSlotId < this.playerInvSlot + 27) {
                if (!this.insertItem(fromStack, this.playerInvSlot + 27, this.playerInvSlot + 36, false)) {
                    return ItemStack.EMPTY;
                }
            // Items transferred from hotbar to inventory
            } else if (fromSlotId >= this.playerInvSlot + 27 && fromSlotId < this.playerInvSlot + 36 &&
                    !this.insertItem(fromStack, this.playerInvSlot, this.playerInvSlot + 27, false)) {
                return ItemStack.EMPTY;
            }

            if (fromStack.isEmpty()) {
                fromSlot.setStack(ItemStack.EMPTY);
            }
            fromSlot.markDirty();

            if (fromStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }
            fromSlot.onTakeItem(player, fromStack);
            this.sendContentUpdates();
        }

        return newStack;
    }

    public static class InputSlot extends Slot {
        public InputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
    }

    public class OutputSlot extends Slot {
        public OutputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            super.onTakeItem(player, stack);

            AutoMechanicTableScreenHandler.this.getSelectedRecipe()
                    .ifPresent(recipe -> {
                        recipe.craft(AutoMechanicTableScreenHandler.this.inputInv);
                        stack.getItem().onCraft(stack, player.getWorld(), player);
                        AutoMechanicTableScreenHandler.this.updateRecipeState();
                    });
        }
    }
}
