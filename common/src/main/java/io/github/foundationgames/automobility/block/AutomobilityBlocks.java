package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import io.github.foundationgames.automobility.intermediary.Intermediary;
import io.github.foundationgames.automobility.item.SlopeBlockItem;
import io.github.foundationgames.automobility.item.SteepSlopeBlockItem;
import io.github.foundationgames.automobility.item.TooltipBlockItem;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public enum AutomobilityBlocks {;
    public static final Block AUTO_MECHANIC_TABLE = register("auto_mechanic_table", new AutoMechanicTableBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)), Automobility.GROUP);
    public static final Block AUTOMOBILE_ASSEMBLER = register("automobile_assembler", new AutomobileAssemblerBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)), Automobility.GROUP);

    public static final Block LAUNCH_GEL = register("launch_gel", new LaunchGelBlock(BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN).sound(SoundType.HONEY_BLOCK).noCollission()), Automobility.COURSE_ELEMENTS);
    public static final Block ALLOW = register("allow", new Block(BlockBehaviour.Properties.copy(Blocks.BARRIER).sound(SoundType.METAL)),
            b -> new TooltipBlockItem(b, Component.translatable("tooltip.block.automobility.allow").withStyle(ChatFormatting.AQUA), new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));

    public static final Block GRASS_OFF_ROAD = register("grass_off_road", new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).noCollission(), AUtils.colorFromInt(0x406918)), Automobility.COURSE_ELEMENTS);
    public static final Block DIRT_OFF_ROAD = register("dirt_off_road", new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).noCollission(), AUtils.colorFromInt(0x594227)), Automobility.COURSE_ELEMENTS);
    public static final Block SAND_OFF_ROAD = register("sand_off_road", new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.SAND).noCollission(), AUtils.colorFromInt(0xC2B185)), Automobility.COURSE_ELEMENTS);
    public static final Block SNOW_OFF_ROAD = register("snow_off_road", new OffRoadBlock(BlockBehaviour.Properties.copy(Blocks.SNOW).noCollission(), AUtils.colorFromInt(0xD0E7ED)), Automobility.COURSE_ELEMENTS);

    public static final Block DASH_PANEL = register("dash_panel", new DashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(s -> 1).emissiveRendering((state, world, pos) -> true).noCollission()), Automobility.COURSE_ELEMENTS);
    public static final Block SLOPED_DASH_PANEL = register("sloped_dash_panel", new SlopedDashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(s -> 1).emissiveRendering((state, world, pos) -> true)));
    public static final Block STEEP_SLOPED_DASH_PANEL = register("steep_sloped_dash_panel", new SteepSlopedDashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(s -> 1).emissiveRendering((state, world, pos) -> true)));

    public static final BlockEntityType<AutomobileAssemblerBlockEntity> AUTOMOBILE_ASSEMBLER_ENTITY = RegistryQueue.register(Registry.BLOCK_ENTITY_TYPE,
            Automobility.rl("automobile_assembler"), Intermediary.get().blockEntity(AutomobileAssemblerBlockEntity::new, AUTOMOBILE_ASSEMBLER));

    public static void init() {
        RegistryQueue.register(Registry.ITEM, Automobility.rl("sloped_dash_panel"), new SlopeBlockItem(null, SLOPED_DASH_PANEL, new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));
        RegistryQueue.register(Registry.ITEM, Automobility.rl("steep_sloped_dash_panel"), new SteepSlopeBlockItem(null, STEEP_SLOPED_DASH_PANEL, new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        Intermediary.get().blockColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D), GRASS_OFF_ROAD);
        Intermediary.get().itemColorProvider((stack, tintIndex) -> GrassColor.get(0.5D, 1.0D), GRASS_OFF_ROAD.asItem());

        Intermediary.get().blockRenderType(LAUNCH_GEL, RenderType.translucent());
    }

    public static Block register(String name, Block block) {
        return RegistryQueue.register(Registry.BLOCK, Automobility.rl(name), block);
    }

    public static Block register(String name, Block block, CreativeModeTab group) {
        return register(name, block, b -> new BlockItem(b, new Item.Properties().tab(group)));
    }

    public static Block register(String name, Block block, Function<Block, BlockItem> item) {
        RegistryQueue.register(Registry.ITEM, Automobility.rl(name), item.apply(block));
        return register(name, block);
    }
}
