package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.item.SlopeBlockItem;
import io.github.foundationgames.automobility.item.SteepSlopeBlockItem;
import io.github.foundationgames.automobility.resource.AutomobilityAssets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public enum AutomobilityBlocks {;
    public static final Block DASH_PANEL = register("dash_panel", new DashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true).noCollision()), Automobility.GROUP);

    public static void init() {
        registerSlopes("minecraft");
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
    }

    public static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, Automobility.id(name), block);
    }

    public static Block register(String name, Block block, ItemGroup group) {
        Registry.register(Registry.ITEM, Automobility.id(name), new BlockItem(block, new Item.Settings().group(group)));
        return register(name, block);
    }

    public static void registerSlopes(String namespace) {
        for (var base : Registry.BLOCK) {
            if (base.getClass().equals(Block.class)) {
                var id = Registry.BLOCK.getId(base);
                if (id.getNamespace().equals(namespace)) {
                    var path = id.getPath()+"_slope";
                    var steepPath = "steep_"+path;
                    var block = register(path, new SlopeBlock(FabricBlockSettings.copyOf(base)));
                    Registry.register(Registry.ITEM, Automobility.id(path), new SlopeBlockItem(base, block, new Item.Settings().group(Automobility.GROUP)));
                    block = register(steepPath, new SteepSlopeBlock(FabricBlockSettings.copyOf(base)));
                    Registry.register(Registry.ITEM, Automobility.id(steepPath), new SteepSlopeBlockItem(base, block, new Item.Settings().group(Automobility.GROUP)));
                    AutomobilityAssets.addProcessor(pack -> AutomobilityAssets.addSlope(path, namespace+":block/"+id.getPath()));
                }
            }
        }
    }
}
