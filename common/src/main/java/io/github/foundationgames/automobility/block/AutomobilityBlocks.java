package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import io.github.foundationgames.automobility.item.DashPanelItem;
import io.github.foundationgames.automobility.item.SlopeBlockItem;
import io.github.foundationgames.automobility.item.SteepSlopeBlockItem;
import io.github.foundationgames.automobility.item.TooltipBlockItem;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.Eventual;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public enum AutomobilityBlocks {;
    public static final Eventual<Block> AUTO_MECHANIC_TABLE = register("auto_mechanic_table", () -> new AutoMechanicTableBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)), Automobility.GROUP);
    public static final Eventual<Block> AUTOMOBILE_ASSEMBLER = register("automobile_assembler", () -> new AutomobileAssemblerBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)), Automobility.GROUP);

    public static final Eventual<Block> SLOPE = register("slope", () -> new SlopeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), false), b -> new SlopeBlockItem(b, new Item.Properties()), Automobility.GROUP);
    public static final Eventual<Block> STEEP_SLOPE = register("steep_slope", () -> new SteepSlopeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), false), b -> new SteepSlopeBlockItem(b, new Item.Properties()), Automobility.GROUP);

    public static final Eventual<Block> SLOPE_WITH_DASH_PANEL = register("slope_with_dash_panel", () -> new SlopeWithDashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .lightLevel(s -> s.getValue(DashPanelBlock.POWERED) ? 0 : 1).emissiveRendering((s, l, p) -> !s.getValue(DashPanelBlock.POWERED))));
    public static final Eventual<Block> STEEP_SLOPE_WITH_DASH_PANEL = register("steep_slope_with_dash_panel", () -> new SteepSlopeWithDashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .lightLevel(s -> s.getValue(DashPanelBlock.POWERED) ? 0 : 1).emissiveRendering((s, l, p) -> !s.getValue(DashPanelBlock.POWERED))));
    public static final Eventual<Block> DASH_PANEL = register("dash_panel", () -> new DashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .lightLevel(s -> s.getValue(DashPanelBlock.POWERED) ? 0 : 1).emissiveRendering((s, l, p) -> !s.getValue(DashPanelBlock.POWERED)).noCollission()), b -> new DashPanelItem(b, new Item.Properties()), Automobility.GROUP);

    public static final Eventual<Block> GRASS_OFF_ROAD = register("grass_off_road", () -> new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).noCollission(), AUtils.colorFromInt(0x406918)), Automobility.GROUP);
    public static final Eventual<Block> DIRT_OFF_ROAD = register("dirt_off_road", () -> new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).noCollission(), AUtils.colorFromInt(0x594227)), Automobility.GROUP);
    public static final Eventual<Block> SAND_OFF_ROAD = register("sand_off_road", () -> new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.SAND).noCollission(), AUtils.colorFromInt(0xC2B185)), Automobility.GROUP);
    public static final Eventual<Block> SNOW_OFF_ROAD = register("snow_off_road", () -> new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.SNOW).noCollission(), AUtils.colorFromInt(0xD0E7ED)), Automobility.GROUP);

    public static final Eventual<Block> LAUNCH_GEL = register("launch_gel", () -> new LaunchGelBlock(BlockBehaviour.Properties.copy(Blocks.CLAY).strength(0.1f).sound(SoundType.HONEY_BLOCK).noCollission()), Automobility.GROUP);

    public static final Eventual<Block> ALLOW = register("allow", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BARRIER).sound(SoundType.METAL)),
            b -> new TooltipBlockItem(b, Component.translatable("tooltip.block.automobility.allow").withStyle(ChatFormatting.AQUA), new Item.Properties()));

    public static final Eventual<BlockEntityType<AutomobileAssemblerBlockEntity>> AUTOMOBILE_ASSEMBLER_ENTITY = RegistryQueue.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Automobility.rl("automobile_assembler"), () -> Platform.get().blockEntity(AutomobileAssemblerBlockEntity::new, AUTOMOBILE_ASSEMBLER.require()));

    public static void init() {
    }

    public static Eventual<Block> register(String name, Supplier<Block> block) {
        return RegistryQueue.register(BuiltInRegistries.BLOCK, Automobility.rl(name), block);
    }

    public static Eventual<Block> register(String name, Supplier<Block> block, Automobility.ItemGroup group) {
        return register(name, block, b -> new BlockItem(b, new Item.Properties()), group);
    }

    public static Eventual<Block> register(String name, Supplier<Block> block, Function<Block, BlockItem> item, Automobility.ItemGroup group) {
        var blockPromise = register(name, block);
        var itemPromise = RegistryQueue.register(BuiltInRegistries.ITEM, Automobility.rl(name), () -> item.apply(blockPromise.require()));

        if (group != null) {
            Automobility.addToItemGroup(group, itemPromise);
        }

        return blockPromise;
    }

    public static Eventual<Block> register(String name, Supplier<Block> block, Function<Block, BlockItem> item) {
        return register(name, block, item, null);
    }
}
