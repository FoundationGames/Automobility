package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.block.AutoMechanicTableBlock;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreenHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class BlockRearAttachment extends RearAttachment {
    public static final Component TITLE_CRAFTING = Component.translatable("container.crafting");
    public static final Component TITLE_LOOM = Component.translatable("container.loom");
    public static final Component TITLE_CARTOGRAPHY = Component.translatable("container.cartography_table");
    public static final Component TITLE_SMITHING = Component.translatable("container.upgrade");
    public static final Component TITLE_GRINDSTONE = Component.translatable("container.grindstone_title");
    public static final Component TITLE_STONECUTTER = Component.translatable("container.stonecutter");

    public final BlockState block;
    private final @Nullable BiFunction<ContainerLevelAccess, BlockRearAttachment, MenuProvider> screenProvider;

    public BlockRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity, BlockState block, @Nullable BiFunction<ContainerLevelAccess, BlockRearAttachment, MenuProvider> screenProvider) {
        super(type, entity);
        this.block = block;
        this.screenProvider = screenProvider;
    }

    @Override
    public boolean hasMenu() {
        return this.screenProvider != null;
    }

    @Override
    public @Nullable MenuProvider createMenu(ContainerLevelAccess ctx) {
        return this.screenProvider != null ? this.screenProvider.apply(ctx, this) : null;
    }

    public static BlockRearAttachment craftingTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.CRAFTING_TABLE.defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) ->
                    new CraftingMenu(syncId, inventory, ctx), TITLE_CRAFTING)
        );
    }

    public static BlockRearAttachment loom(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.LOOM.defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) ->
                        new LoomMenu(syncId, inventory, ctx), TITLE_LOOM)
        );
    }

    public static BlockRearAttachment cartographyTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.CARTOGRAPHY_TABLE.defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) ->
                        new CartographyTableMenu(syncId, inventory, ctx), TITLE_CARTOGRAPHY)
        );
    }

    public static BlockRearAttachment smithingTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.SMITHING_TABLE.defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) ->
                        new SmithingMenu(syncId, inventory, ctx), TITLE_SMITHING)
        );
    }

    public static BlockRearAttachment grindstone(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.GRINDSTONE.defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) ->
                        new GrindstoneMenu(syncId, inventory, ctx), TITLE_GRINDSTONE)
        );
    }

    public static BlockRearAttachment stonecutter(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                Blocks.STONECUTTER.defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) ->
                        new StonecutterMenu(syncId, inventory, ctx), TITLE_STONECUTTER)
        );
    }

    public static BlockRearAttachment autoMechanicTable(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BlockRearAttachment(type, entity,
                AutomobilityBlocks.AUTO_MECHANIC_TABLE.require().defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) ->
                        new AutoMechanicTableScreenHandler(syncId, inventory, ctx), AutoMechanicTableBlock.UI_TITLE)
        );
    }
}
