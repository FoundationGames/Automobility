package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class ChestRearAttachment extends BaseChestRearAttachment implements Container, MenuProvider {
    private NonNullList<ItemStack> inventory;

    public ChestRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity, BlockState block, @Nullable BiFunction<ContainerLevelAccess, BlockRearAttachment, MenuProvider> screenProvider) {
        super(type, entity, block, screenProvider);
        this.inventory = NonNullList.withSize(27, ItemStack.EMPTY);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        var pos = this.pos();
        this.inventory.forEach(s -> Containers.dropItemStack(this.world(), pos.x, pos.y, pos.z, s));
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public void setChanged() {
        this.automobile.markDirty();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public Component getDisplayName() {
        return BaseChestRearAttachment.TITLE_CHEST;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return ChestMenu.threeRows(syncId, inv, this);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public void startOpen(Player player) {
        this.open(player);
    }

    @Override
    public void stopOpen(Player player) {
        this.close(player);
    }

    @Override
    protected SoundEvent getOpenSound() {
        return SoundEvents.CHEST_OPEN;
    }

    @Override
    protected SoundEvent getCloseSound() {
        return SoundEvents.CHEST_CLOSE;
    }

    @Override
    public void writeNbt(CompoundTag nbt) {
        super.writeNbt(nbt);

        nbt.put("Items", ContainerHelper.saveAllItems(new CompoundTag(), this.inventory));
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);

        ContainerHelper.loadAllItems(nbt.getCompound("Items"), this.inventory);
    }
}
