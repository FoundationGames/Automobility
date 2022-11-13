package io.github.foundationgames.automobility.automobile.attachment.rear;

import com.mojang.datafixers.util.Pair;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.screen.SingleSlotScreenHandler;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.registry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BannerPostRearAttachment extends RearAttachment {
    private static final Text UI_TITLE = Text.translatable("container.automobility.banner_post");

    private @Nullable DyeColor baseColor = null;

    private NbtList patternNbt = new NbtList();
    private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;

    public final Inventory inventory = new SimpleInventory(1) {
        @Override
        public void setStack(int slot, ItemStack stack) {
            super.setStack(slot, stack);

            BannerPostRearAttachment.this.setFromItem(stack);
        }
    };

    public BannerPostRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    public void sendPacket() {
        var nbt = new NbtCompound();
        this.putToNbt(nbt);

        if (!this.world().isClient()) {
            this.automobile().forNearbyPlayers(200, false, p ->
                    PayloadPackets.sendBannerPostAttachmentUpdatePacket(this.automobile(), nbt, p));
        }
    }

    @Override
    public void updatePacketRequested(ServerPlayerEntity player) {
        super.updatePacketRequested(player);

        var nbt = new NbtCompound();
        this.putToNbt(nbt);
        PayloadPackets.sendBannerPostAttachmentUpdatePacket(this.automobile(), nbt, player);
    }

    public void putToNbt(NbtCompound nbt) {
        if (this.baseColor != null) {
            nbt.putInt("Color", this.baseColor.getId());
        }

        if (this.patternNbt != null) {
            nbt.put("Patterns", this.patternNbt);
        }
    }

    public void setFromNbt(NbtCompound nbt) {
        if (nbt.contains("Color")) {
            this.baseColor = DyeColor.byId(nbt.getInt("Color"));
        } else {
            this.baseColor = null;
        }

        if (nbt.contains("Patterns", 9)) {
            this.patternNbt = nbt.getList("Patterns", 10);
        } else {
            this.patternNbt = new NbtList();
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

        var nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt != null && nbt.contains("Patterns", 9)) {
            this.patternNbt = nbt.getList("Patterns", 10);
        } else {
            this.patternNbt = new NbtList();
        }
        this.patterns = null;

        if (!this.world().isClient()) {
            this.sendPacket();
        }
    }

    public void erase() {
        this.baseColor = null;

        if (!this.world().isClient()) {
            this.sendPacket();
        }
    }

    public @Nullable DyeColor getBaseColor() {
        return this.baseColor;
    }

    public List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = BannerBlockEntity.getPatternsFromNbt(this.baseColor, this.patternNbt);
        }

        return this.patterns;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        var pos = this.pos();
        ItemScatterer.spawn(this.world(), pos.x, pos.y, pos.z, this.inventory.getStack(0));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        this.putToNbt(nbt);

        var item = new NbtCompound();
        this.inventory.getStack(0).writeNbt(item);

        nbt.put("Banner", item);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.setFromNbt(nbt);

        this.inventory.setStack(0, ItemStack.fromNbt(nbt.getCompound("Banner")));
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public @Nullable NamedScreenHandlerFactory createMenu(ScreenHandlerContext ctx) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInv, player) ->
                new SingleSlotScreenHandler(syncId, playerInv, this.inventory), UI_TITLE);
    }
}
