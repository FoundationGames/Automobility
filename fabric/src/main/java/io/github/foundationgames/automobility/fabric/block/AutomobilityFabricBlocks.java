package io.github.foundationgames.automobility.fabric.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.SlopeBlock;
import io.github.foundationgames.automobility.block.SteepSlopeBlock;
import io.github.foundationgames.automobility.fabric.block.old.SlopedDashPanelBlock;
import io.github.foundationgames.automobility.fabric.block.old.SteepSlopedDashPanelBlock;
import io.github.foundationgames.automobility.fabric.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.fabric.resource.AutomobilityData;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class AutomobilityFabricBlocks {
    public static final Block SLOPED_DASH_PANEL = register("sloped_dash_panel", new SlopedDashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(s -> 1).emissiveRendering((state, world, pos) -> true)));
    public static final Block STEEP_SLOPED_DASH_PANEL = register("steep_sloped_dash_panel", new SteepSlopedDashPanelBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).lightLevel(s -> 1).emissiveRendering((state, world, pos) -> true)));

    public static void init() {
        registerSlopes("minecraft");
    }

    public static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Automobility.rl(name), block);
    }

    public static Block register(String name, Block block, Function<Block, BlockItem> item) {
        Registry.register(BuiltInRegistries.ITEM, Automobility.rl(name), item.apply(block));
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
        for (var base : BuiltInRegistries.BLOCK) {
            if (base.getClass().equals(Block.class)) {
                var id = BuiltInRegistries.BLOCK.getKey(base);
                if (id.getNamespace().equals(namespace)) {
                    var path = id.getPath()+"_slope";
                    var steepPath = "steep_"+path;
                    register(path, new SlopeBlock(FabricBlockSettings.copyOf(base), true));
                    var normalId = Automobility.rl(path);
                    var steepId = Automobility.rl(steepPath);
                    register(steepPath, new SteepSlopeBlock(FabricBlockSettings.copyOf(base), true));
                    AutomobilityAssets.addProcessor(pack -> AutomobilityAssets.addMinecraftSlope(path, id.getPath()));
                    AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(normalId);
                    AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(steepId);
                }
            }

            makeStairsSticky(base, BuiltInRegistries.BLOCK.getKey(base));
        }

        RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((raw, id, block) -> {
            makeStairsSticky(block, id);
        });
    }
}
