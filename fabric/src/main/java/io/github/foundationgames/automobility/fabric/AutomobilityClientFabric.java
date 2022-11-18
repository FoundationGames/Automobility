package io.github.foundationgames.automobility.fabric;

import io.github.foundationgames.automobility.AutomobilityClient;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.fabric.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.particle.DriftSmokeParticle;
import io.github.foundationgames.automobility.screen.AutomobileHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;

public class AutomobilityClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricPlatform.init();

        AutomobilityClient.init();
        AutomobilityAssets.setup();

        ParticleFactoryRegistry.getInstance().register(AutomobilityParticles.DRIFT_SMOKE.require(), DriftSmokeParticle.Factory::new);
        HudRenderCallback.EVENT.register((pose, tickDelta) -> {
            var player = Minecraft.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                AutomobileHud.render(pose, player, auto, tickDelta);
            }
        });

        ColorProviderRegistry.BLOCK.register(AutomobilityBlocks.GRASS_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
        ColorProviderRegistry.ITEM.register(AutomobilityBlocks.GRASS_ITEM_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
    }
}
