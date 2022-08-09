package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.block.entity.render.AutomobileAssemblerBlockEntityRenderer;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreen;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.LiteralText;

public class AutomobilityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AutomobilityModels.init();
        AutomobilityBlocks.initClient();
        AutomobilityItems.initClient();
        AutomobilityEntities.initClient();
        AutomobilityParticles.initClient();
        PayloadPackets.initClient();

        AutomobilityAssets.setup();

        HandledScreens.register(Automobility.AUTO_MECHANIC_SCREEN, AutoMechanicTableScreen::new);

        HudRenderCallback.EVENT.register((matrices, delta) -> {
            var player = MinecraftClient.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                float speed = Math.abs(auto.getHSpeed() * 20);
                int color = 0xFFFFFF;
                if (auto.getBoostTimer() > 0) color = 0xFF6F00;
                if (auto.getTurboCharge() > AutomobileEntity.SMALL_TURBO_TIME) color = 0xFFEA4A;
                if (auto.getTurboCharge() > AutomobileEntity.MEDIUM_TURBO_TIME) color = 0x7DE9FF;
                if (auto.getTurboCharge() > AutomobileEntity.LARGE_TURBO_TIME) color = 0x906EFF;
                DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, new LiteralText(AUtils.DEC_TWO_PLACES.format(speed) +" m/s"), 20, 20, color);
            }
        });

        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.AUTOMOBILE_ASSEMBLER, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(AutomobilityBlocks.AUTOMOBILE_ASSEMBLER_ENTITY, AutomobileAssemblerBlockEntityRenderer::new);
    }
}
