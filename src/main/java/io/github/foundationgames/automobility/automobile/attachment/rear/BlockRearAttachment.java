package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.block.AutoMechanicTableBlock;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreenHandler;
import io.github.foundationgames.automobility.util.duck.EnderChestInventoryDuck;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BlockRearAttachment extends RearAttachment {
    public static final Text TITLE_CRAFTING = new TranslatableText("container.crafting");
    public static final Text TITLE_LOOM = new TranslatableText("container.loom");
    public static final Text TITLE_CARTOGRAPHY = new TranslatableText("container.cartography_table");
    public static final Text TITLE_SMITHING = new TranslatableText("container.upgrade");
    public static final Text TITLE_GRINDSTONE = new TranslatableText("container.grindstone_title");
    public static final Text TITLE_STONECUTTER = new TranslatableText("container.stonecutter");

    public final BlockState block;
    private final @Nullable BiFunction<ScreenHandlerContext, BlockRearAttachment, NamedScreenHandlerFactory> screenProvider;

    public BlockRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity, BlockState block, @Nullable BiFunction<ScreenHandlerContext, BlockRearAttachment, NamedScreenHandlerFactory> screenProvider) {
        super(type, entity);
        this.block = block;
        this.screenProvider = screenProvider;
    }

    @Override
    public boolean hasMenu() {
        return this.screenProvider != null;
    }

    @Override
    public @Nullable NamedScreenHandlerFactory createMenu(ScreenHandlerContext ctx) {
        return this.screenProvider != null ? this.screenProvider.apply(ctx, this) : null;
    }

    public static BlockRearAttachment craftingTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.CRAFTING_TABLE.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                    new CraftingScreenHandler(syncId, inventory, ctx), TITLE_CRAFTING)
        );
    }

    public static BlockRearAttachment loom(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.LOOM.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                        new LoomScreenHandler(syncId, inventory, ctx), TITLE_LOOM)
        );
    }

    public static BlockRearAttachment cartographyTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.CARTOGRAPHY_TABLE.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                        new CartographyTableScreenHandler(syncId, inventory, ctx), TITLE_CARTOGRAPHY)
        );
    }

    public static BlockRearAttachment smithingTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.SMITHING_TABLE.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                        new SmithingScreenHandler(syncId, inventory, ctx), TITLE_SMITHING)
        );
    }

    public static BlockRearAttachment grindstone(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.GRINDSTONE.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                        new GrindstoneScreenHandler(syncId, inventory, ctx), TITLE_GRINDSTONE)
        );
    }

    public static BlockRearAttachment stonecutter(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.STONECUTTER.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                        new StonecutterScreenHandler(syncId, inventory, ctx), TITLE_STONECUTTER)
        );
    }

    public static BlockRearAttachment autoMechanicTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                AutomobilityBlocks.AUTO_MECHANIC_TABLE.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                        new AutoMechanicTableScreenHandler(syncId, inventory, ctx), AutoMechanicTableBlock.UI_TITLE)
        );
    }
}
