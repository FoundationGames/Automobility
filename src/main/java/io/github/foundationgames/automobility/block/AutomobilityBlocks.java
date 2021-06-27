package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.Automobility;
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
    public static final Block DASH_PANEL = register("dash_panel", new DashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true).noCollision()), ItemGroup.TRANSPORTATION);
    public static final Block STEEP_STONE_SLOPE = register("steep_stone_slope", new SteepSlopeBlock(FabricBlockSettings.copyOf(Blocks.STONE)), ItemGroup.TRANSPORTATION);
    public static final Block STEEP_COBBLESTONE_SLOPE = register("steep_cobblestone_slope", new SteepSlopeBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE)), ItemGroup.TRANSPORTATION);

    public static void init() {
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
}
