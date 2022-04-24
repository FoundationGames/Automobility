package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.resource.AutomobilityData;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.lambdacontrols.ControllerUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Automobility implements ModInitializer {
    public static final String MOD_ID = "automobility";

    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(Automobility.id("automobility"), AUtils::createGroupIcon);
    public static final ItemGroup PREFABS = FabricItemGroupBuilder.build(Automobility.id("automobility_prefabs"), AUtils::createPrefabsIcon);

    public static final TagKey<Block> SLOPES = TagKey.of(Registry.BLOCK_KEY, Automobility.id("slopes"));
    public static final TagKey<Block> STEEP_SLOPES = TagKey.of(Registry.BLOCK_KEY, Automobility.id("steep_slopes"));
    public static final TagKey<Block> NON_STEEP_SLOPES = TagKey.of(Registry.BLOCK_KEY, Automobility.id("non_steep_slopes"));
    public static final TagKey<Block> STICKY_SLOPES = TagKey.of(Registry.BLOCK_KEY, Automobility.id("sticky_slopes"));

    @Override
    public void onInitialize() {
        AutomobilityItems.init();
        AutomobilityBlocks.init();
        AutomobilityEntities.init();
        PayloadPackets.init();

        AutomobilityData.setup();

        ControllerUtils.initLCHandler();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
