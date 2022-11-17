package io.github.foundationgames.automobility.fabric.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.block.SlopeBlock;
import io.github.foundationgames.automobility.block.SteepSlopeBlock;
import io.github.foundationgames.automobility.fabric.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.fabric.resource.AutomobilityData;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;

import java.util.function.Function;

public class AutomobilityFabricBlocks {
    public static void init() {
        registerSlopes("minecraft");
    }

    public static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, Automobility.rl(name), block);
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
                    register(path, new SlopeBlock(FabricBlockSettings.copyOf(base)));
                    var normalId = Automobility.rl(path);
                    var steepId = Automobility.rl(steepPath);
                    register(steepPath, new SteepSlopeBlock(FabricBlockSettings.copyOf(base)));
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
