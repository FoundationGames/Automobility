package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.block.entity.render.AutomobileAssemblerBlockEntityRenderer;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.intermediary.Intermediary;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreen;
import io.github.foundationgames.automobility.screen.SingleSlotScreen;
import io.github.foundationgames.automobility.util.network.ClientPackets;
import net.minecraft.client.renderer.RenderType;

public class AutomobilityClient {
    public static void init() {
        AutomobilityModels.init();
        AutomobilityBlocks.initClient();
        AutomobilityItems.initClient();
        AutomobilityEntities.initClient();
        AutomobilityParticles.initClient();
        ClientPackets.initClient();

        //AutomobilityAssets.setup();

        Intermediary.get().registerMenuScreen(Automobility.AUTO_MECHANIC_SCREEN, AutoMechanicTableScreen::new);
        Intermediary.get().registerMenuScreen(Automobility.SINGLE_SLOT_SCREEN, SingleSlotScreen::new);

        Intermediary.get().blockRenderType(AutomobilityBlocks.AUTOMOBILE_ASSEMBLER, RenderType.cutout());

        Intermediary.get().blockEntityRenderer(AutomobilityBlocks.AUTOMOBILE_ASSEMBLER_ENTITY, AutomobileAssemblerBlockEntityRenderer::new);
    }
}
