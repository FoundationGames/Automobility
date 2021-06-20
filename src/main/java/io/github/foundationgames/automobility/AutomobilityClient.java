package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import net.fabricmc.api.ClientModInitializer;

public class AutomobilityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AutomobilityBlocks.initClient();
        AutomobilityItems.initClient();
        AutomobilityEntities.initClient();
    }
}
