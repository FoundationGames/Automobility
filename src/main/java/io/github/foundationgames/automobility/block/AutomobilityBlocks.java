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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public enum AutomobilityBlocks {;
    public static final Block AUTO_MECHANIC_TABLE = register("auto_mechanic_table", new AutoMechanicTableBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)), Automobility.GROUP);
    public static final Block AUTOMOBILE_ASSEMBLER = register("automobile_assembler", new AutomobileAssemblerBlock(FabricBlockSettings.copyOf(Blocks.ANVIL)), Automobility.GROUP);

    public static final Block LAUNCH_GEL = register("launch_gel", new LaunchGelBlock(FabricBlockSettings.copyOf(Blocks.GLOW_LICHEN).sounds(BlockSoundGroup.HONEY).noCollision()), Automobility.COURSE_ELEMENTS);
    public static final Block ALLOW = register("allow", new Block(FabricBlockSettings.copyOf(Blocks.BARRIER).sounds(BlockSoundGroup.METAL)),
            b -> new TooltipBlockItem(b, new TranslatableText("tooltip.block.automobility.allow").formatted(Formatting.AQUA), new Item.Settings().group(Automobility.COURSE_ELEMENTS)));

    public static final Block GRASS_OFF_ROAD = register("grass_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).noCollision(), AUtils.colorFromInt(0x406918)), Automobility.COURSE_ELEMENTS);
    public static final Block DIRT_OFF_ROAD = register("dirt_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.DIRT).noCollision(), AUtils.colorFromInt(0x594227)), Automobility.COURSE_ELEMENTS);
    public static final Block SAND_OFF_ROAD = register("sand_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SAND).noCollision(), AUtils.colorFromInt(0xC2B185)), Automobility.COURSE_ELEMENTS);
    public static final Block SNOW_OFF_ROAD = register("snow_off_road", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SNOW).noCollision(), AUtils.colorFromInt(0xD0E7ED)), Automobility.COURSE_ELEMENTS);

    public static final Block DASH_PANEL = register("dash_panel", new DashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true).noCollision()), Automobility.COURSE_ELEMENTS);
    public static final Block SLOPED_DASH_PANEL = register("sloped_dash_panel", new SlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true)));
    public static final Block STEEP_SLOPED_DASH_PANEL = register("steep_sloped_dash_panel", new SteepSlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true)));

    public static final BlockEntityType<AutomobileAssemblerBlockEntity> AUTOMOBILE_ASSEMBLER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
            Automobility.id("automobile_assembler"), FabricBlockEntityTypeBuilder.create(AutomobileAssemblerBlockEntity::new, AUTOMOBILE_ASSEMBLER).build());

    public static void init() {
        Registry.register(Registry.ITEM, Automobility.id("sloped_dash_panel"), new SlopeBlockItem(null, SLOPED_DASH_PANEL, new Item.Settings().group(Automobility.COURSE_ELEMENTS)));
        Registry.register(Registry.ITEM, Automobility.id("steep_sloped_dash_panel"), new SteepSlopeBlockItem(null, STEEP_SLOPED_DASH_PANEL, new Item.Settings().group(Automobility.COURSE_ELEMENTS)));
        registerSlopes("minecraft");
    }

    
    public static void initClient() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D), GRASS_OFF_ROAD);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5D, 1.0D), GRASS_OFF_ROAD.asItem());

        BlockRenderLayerMap.INSTANCE.putBlock(LAUNCH_GEL, RenderLayer.getTranslucent());
    }

    public static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, Automobility.id(name), block);
    }

    public static Block register(String name, Block block, ItemGroup group) {
        return register(name, block, b -> new BlockItem(b, new Item.Settings().group(group)));
    }

    public static Block register(String name, Block block, Function<Block, BlockItem> item) {
        Registry.register(Registry.ITEM, Automobility.id(name), item.apply(block));
        return register(name, block);
    }

    private static void makeStairsSticky(Block candidate, Identifier id) {
        if (candidate instanceof StairsBlock) {
            AutomobilityData.STICKY_SLOPE_TAG_CANDIDATES.add(id);
            AutomobilityData.STICKY_SLOPE_TAG_CANDIDATES.add(id);
        }
    }

    public static void registerSlopes(String namespace) {
        AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.id("sloped_dash_panel"));
        AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.id("steep_sloped_dash_panel"));
        for (var base : Registry.BLOCK) {
            if (base.getClass().equals(Block.class)) {
                var id = Registry.BLOCK.getId(base);
                if (id.getNamespace().equals(namespace)) {
                    var path = id.getPath()+"_slope";
                    var steepPath = "steep_"+path;
                    var block = register(path, new SlopeBlock(FabricBlockSettings.copyOf(base)));
                    var normalId = Automobility.id(path);
                    var steepId = Automobility.id(steepPath);
                    Registry.register(Registry.ITEM, normalId, new SlopeBlockItem(base, block, new Item.Settings().group(Automobility.COURSE_ELEMENTS)));
                    block = register(steepPath, new SteepSlopeBlock(FabricBlockSettings.copyOf(base)));
                    Registry.register(Registry.ITEM, steepId, new SteepSlopeBlockItem(base, block, new Item.Settings().group(Automobility.COURSE_ELEMENTS)));
                    AutomobilityAssets.addProcessor(pack -> AutomobilityAssets.addMinecraftSlope(path, id.getPath()));
                    AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(normalId);
                    AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(steepId);
                }
            }

            makeStairsSticky(base, Registry.BLOCK.getId(base));
        }

        RegistryEntryAddedCallback.event(Registry.BLOCK).register((raw, id, block) -> {
            makeStairsSticky(block, id);
        });
    }
}
