package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import io.github.foundationgames.automobility.item.SlopeBlockItem;
import io.github.foundationgames.automobility.item.SteepSlopeBlockItem;
import io.github.foundationgames.automobility.item.TooltipBlockItem;
import io.github.foundationgames.automobility.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.resource.AutomobilityData;
import io.github.foundationgames.automobility.util.AUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.function.Function;

public enum AutomobilityBlocks {;
    public static final Block AUTO_MECHANIC_TABLE = register("auto_mechanic_table", new AutoMechanicTableBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)), Automobility.GROUP);
    public static final Block AUTOMOBILE_ASSEMBLER = register("automobile_assembler", new AutomobileAssemblerBlock(FabricBlockSettings.copyOf(Blocks.ANVIL)), Automobility.GROUP);

    public static final Block LAUNCH_GEL = register("launch_gel", new LaunchGelBlock(FabricBlockSettings.copyOf(Blocks.GLOW_LICHEN).sound(SoundType.HONEY_BLOCK).noCollission()), Automobility.COURSE_ELEMENTS);
    public static final Block ALLOW = register("allow", new Block(FabricBlockSettings.copyOf(Blocks.BARRIER).sound(SoundType.METAL)),
            b -> new TooltipBlockItem(b, Component.translatable("tooltip.block.automobility.allow").withStyle(ChatFormatting.AQUA), new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));

    public static final Block GRASS_OFF_ROAD = register("grass_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).noCollission(), AUtils.colorFromInt(0x406918)), Automobility.COURSE_ELEMENTS);
    public static final Block DIRT_OFF_ROAD = register("dirt_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.DIRT).noCollission(), AUtils.colorFromInt(0x594227)), Automobility.COURSE_ELEMENTS);
    public static final Block SAND_OFF_ROAD = register("sand_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SAND).noCollission(), AUtils.colorFromInt(0xC2B185)), Automobility.COURSE_ELEMENTS);
    public static final Block SNOW_OFF_ROAD = register("snow_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SNOW).noCollission(), AUtils.colorFromInt(0xD0E7ED)), Automobility.COURSE_ELEMENTS);

    public static final Block DASH_PANEL = register("dash_panel", new DashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveRendering((state, world, pos) -> true).noCollission()), Automobility.COURSE_ELEMENTS);
    public static final Block SLOPED_DASH_PANEL = register("sloped_dash_panel", new SlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveRendering((state, world, pos) -> true)));
    public static final Block STEEP_SLOPED_DASH_PANEL = register("steep_sloped_dash_panel", new SteepSlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveRendering((state, world, pos) -> true)));

    public static final BlockEntityType<AutomobileAssemblerBlockEntity> AUTOMOBILE_ASSEMBLER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
            Automobility.rl("automobile_assembler"), FabricBlockEntityTypeBuilder.create(AutomobileAssemblerBlockEntity::new, AUTOMOBILE_ASSEMBLER).build());

    public static void init() {
        Registry.register(Registry.ITEM, Automobility.rl("sloped_dash_panel"), new SlopeBlockItem(null, SLOPED_DASH_PANEL, new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));
        Registry.register(Registry.ITEM, Automobility.rl("steep_sloped_dash_panel"), new SteepSlopeBlockItem(null, STEEP_SLOPED_DASH_PANEL, new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));
        registerSlopes("minecraft");
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D), GRASS_OFF_ROAD);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColor.get(0.5D, 1.0D), GRASS_OFF_ROAD.asItem());

        BlockRenderLayerMap.INSTANCE.putBlock(LAUNCH_GEL, RenderType.translucent());
    }

    public static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, Automobility.rl(name), block);
    }

    public static Block register(String name, Block block, CreativeModeTab group) {
        return register(name, block, b -> new BlockItem(b, new Item.Properties().tab(group)));
    }

    public static Block register(String name, Block block, Function<Block, BlockItem> item) {
        Registry.register(Registry.ITEM, Automobility.rl(name), item.apply(block));
        return register(name, block);
    }

    private static void makeStairsSticky(Block candidate, ResourceLocation id) {
        if (candidate instanceof StairBlock) {
            AutomobilityData.STICKY_SLOPE_TAG_CANDIDATES.add(id);
            AutomobilityData.STICKY_SLOPE_TAG_CANDIDATES.add(id);
        }
    }

    public static void registerSlopes(String namespace) {
        AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.rl("sloped_dash_panel"));
        AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.rl("steep_sloped_dash_panel"));
        for (var base : Registry.BLOCK) {
            if (base.getClass().equals(Block.class)) {
                var id = Registry.BLOCK.getKey(base);
                if (id.getNamespace().equals(namespace)) {
                    var path = id.getPath()+"_slope";
                    var steepPath = "steep_"+path;
                    var block = register(path, new SlopeBlock(FabricBlockSettings.copyOf(base)));
                    var normalId = Automobility.rl(path);
                    var steepId = Automobility.rl(steepPath);
                    Registry.register(Registry.ITEM, normalId, new SlopeBlockItem(base, block, new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));
                    block = register(steepPath, new SteepSlopeBlock(FabricBlockSettings.copyOf(base)));
                    Registry.register(Registry.ITEM, steepId, new SteepSlopeBlockItem(base, block, new Item.Properties().tab(Automobility.COURSE_ELEMENTS)));
                    AutomobilityAssets.addProcessor(pack -> AutomobilityAssets.addMinecraftSlope(path, id.getPath()));
                    AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(normalId);
                    AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(steepId);
                }
            }

            makeStairsSticky(base, Registry.BLOCK.getKey(base));
        }

        RegistryEntryAddedCallback.event(Registry.BLOCK).register((raw, id, block) -> {
            makeStairsSticky(block, id);
        });
    }
}
