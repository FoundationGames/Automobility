package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.lambdacontrols.AutomobilityLC;
import io.github.foundationgames.automobility.util.lambdacontrols.ControllerUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class Automobility implements ModInitializer {
    public static final String MOD_ID = "automobility";

    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(Automobility.id("automobility"), AUtils::createGroupIcon);
    public static final ItemGroup PREFABS = FabricItemGroupBuilder.build(Automobility.id("automobility_prefabs"), AUtils::createPrefabsIcon);

    @Override
    public void onInitialize() {
        AutomobilityItems.init();
        AutomobilityBlocks.init();
        AutomobilityEntities.init();
        PayloadPackets.init();

        ControllerUtils.initLCHandler();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
