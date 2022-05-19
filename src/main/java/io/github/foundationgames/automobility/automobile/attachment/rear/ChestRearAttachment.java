package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class ChestRearAttachment extends BaseChestRearAttachment implements Inventory, NamedScreenHandlerFactory {
    private DefaultedList<ItemStack> inventory;

    public ChestRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity, BlockState block, @Nullable BiFunction<ScreenHandlerContext, BlockRearAttachment, NamedScreenHandlerFactory> screenProvider) {
        super(type, entity, block, screenProvider);
        this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        var pos = this.pos();
        this.inventory.forEach(s -> ItemScatterer.spawn(this.world(), pos.x, pos.y, pos.z, s));
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public void markDirty() {
        this.automobile.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public Text getDisplayName() {
        return BaseChestRearAttachment.TITLE_CHEST;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, inv, this);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.open(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.close(player);
    }

    @Override
    protected SoundEvent getOpenSound() {
        return SoundEvents.BLOCK_CHEST_OPEN;
    }

    @Override
    protected SoundEvent getCloseSound() {
        return SoundEvents.BLOCK_CHEST_CLOSE;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("Items", Inventories.writeNbt(new NbtCompound(), this.inventory));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("Items"), this.inventory);
    }
}
