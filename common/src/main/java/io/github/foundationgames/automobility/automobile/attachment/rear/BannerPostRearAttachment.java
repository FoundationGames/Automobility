package io.github.foundationgames.automobility.automobile.attachment.rear;

import com.mojang.datafixers.util.Pair;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.screen.SingleSlotScreenHandler;
import io.github.foundationgames.automobility.util.network.CommonPackets;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BannerPostRearAttachment extends RearAttachment {
    private static final Component UI_TITLE = Component.translatable("container.automobility.banner_post");

    private @Nullable DyeColor baseColor = null;

    private ListTag patternNbt = new ListTag();
    private List<Pair<Holder<BannerPattern>, DyeColor>> patterns;

    public final Container inventory = new SimpleContainer(1) {
        @Override
        public void setItem(int slot, ItemStack stack) {
            super.setItem(slot, stack);

            BannerPostRearAttachment.this.setFromItem(stack);
        }
    };

    public BannerPostRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    public void sendPacket() {
        var nbt = new CompoundTag();
        this.putToNbt(nbt);

        if (!this.world().isClientSide()) {
            this.automobile().forNearbyPlayers(200, false, p ->
                    CommonPackets.sendBannerPostAttachmentUpdatePacket(this.automobile(), nbt, p));
        }
    }

    @Override
    public void updatePacketRequested(ServerPlayer player) {
        super.updatePacketRequested(player);

        var nbt = new CompoundTag();
        this.putToNbt(nbt);
        CommonPackets.sendBannerPostAttachmentUpdatePacket(this.automobile(), nbt, player);
    }

    public void putToNbt(CompoundTag nbt) {
        if (this.baseColor != null) {
            nbt.putInt("Color", this.baseColor.getId());
        }

        if (this.patternNbt != null) {
            nbt.put("Patterns", this.patternNbt);
        }
    }

    public void setFromNbt(CompoundTag nbt) {
        if (nbt.contains("Color")) {
            this.baseColor = DyeColor.byId(nbt.getInt("Color"));
        } else {
            this.baseColor = null;
        }

        if (nbt.contains("Patterns", 9)) {
            this.patternNbt = nbt.getList("Patterns", 10);
        } else {
            this.patternNbt = new ListTag();
        }
        this.patterns = null;
    }

    public void setFromItem(ItemStack stack) {
        if (stack.getItem() instanceof BannerItem banner) {
            this.baseColor = banner.getColor();
        } else {
            this.erase();
            return;
        }

        var nbt = BlockItem.getBlockEntityData(stack);
        if (nbt != null && nbt.contains("Patterns", 9)) {
            this.patternNbt = nbt.getList("Patterns", 10);
        } else {
            this.patternNbt = new ListTag();
        }
        this.patterns = null;

        if (!this.world().isClientSide()) {
            this.sendPacket();
        }
    }

    public void erase() {
        this.baseColor = null;

        if (!this.world().isClientSide()) {
            this.sendPacket();
        }
    }

    public @Nullable DyeColor getBaseColor() {
        return this.baseColor;
    }

    public List<Pair<Holder<BannerPattern>, DyeColor>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = BannerBlockEntity.createPatterns(this.baseColor, this.patternNbt);
        }

        return this.patterns;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        var pos = this.pos();
        Containers.dropItemStack(this.world(), pos.x, pos.y, pos.z, this.inventory.getItem(0));
    }

    @Override
    public void writeNbt(CompoundTag nbt) {
        super.writeNbt(nbt);
        this.putToNbt(nbt);

        var item = new CompoundTag();
        this.inventory.getItem(0).save(item);

        nbt.put("Banner", item);
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.setFromNbt(nbt);

        this.inventory.setItem(0, ItemStack.of(nbt.getCompound("Banner")));
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public @Nullable MenuProvider createMenu(ContainerLevelAccess ctx) {
        return new SimpleMenuProvider((syncId, playerInv, player) ->
                new SingleSlotScreenHandler(syncId, playerInv, this.inventory), UI_TITLE);
    }
}
