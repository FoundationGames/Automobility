package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreen;
import io.github.foundationgames.automobility.screen.SingleSlotScreen;
import io.github.foundationgames.automobility.util.network.ClientPackets;

public class AutomobilityClient {
    public static void init() {
        AutomobilityModels.init();
        AutomobilityBlocks.initClient();
        AutomobilityItems.initClient();
        AutomobilityEntities.initClient();
        ClientPackets.initClient();

        //AutomobilityAssets.setup();

        Platform.get().registerMenuScreen(Automobility.AUTO_MECHANIC_SCREEN.require(), AutoMechanicTableScreen::new);
        Platform.get().registerMenuScreen(Automobility.SINGLE_SLOT_SCREEN.require(), SingleSlotScreen::new);
    }
}
