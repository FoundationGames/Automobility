package io.github.foundationgames.automobility.fabric;

import io.github.foundationgames.automobility.AutomobilityClient;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.block.model.SlopeBakedModel;
import io.github.foundationgames.automobility.block.model.SlopeUnbakedModel;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.fabric.block.render.FabricSlopeBakedModel;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.particle.DriftSmokeParticle;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.screen.AutomobileHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;

public class AutomobilityClientFabric implements ClientModInitializer {
    private static boolean wasRidingAutomobile = false;

    @Override
    public void onInitializeClient() {
        FabricPlatform.init();

        AutomobilityClient.init();

        ParticleFactoryRegistry.getInstance().register(AutomobilityParticles.DRIFT_SMOKE.require(), DriftSmokeParticle.Factory::new);
        HudRenderCallback.EVENT.register((pose, tickDelta) -> {
            var player = Minecraft.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                AutomobileHud.render(pose, player, auto, tickDelta);
            }
        });

        ColorProviderRegistry.BLOCK.register(AutomobilityClient.GRASS_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
        ColorProviderRegistry.ITEM.register(AutomobilityClient.GRASS_ITEM_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());

        SlopeBakedModel.impl = FabricSlopeBakedModel::new;

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager -> (location, context) ->
                SlopeUnbakedModel.DEFAULT_MODELS.containsKey(location) ? SlopeUnbakedModel.DEFAULT_MODELS.get(location).get() : null);

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            boolean isRidingAutomobile = Minecraft.getInstance().player != null &&
                    Minecraft.getInstance().player.getVehicle() instanceof AutomobileEntity;

            if (wasRidingAutomobile && !isRidingAutomobile) {
                var con = Platform.get().controller();

                con.updateMaxChargeRumbleState(false);
                con.updateOffRoadRumbleState(false);
                con.updateBoostingRumbleState(false, 0);
            }

            wasRidingAutomobile = isRidingAutomobile;
        });

        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.LAUNCH_GEL.require(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.AUTOMOBILE_ASSEMBLER.require(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.SLOPE.require(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.STEEP_SLOPE.require(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.SLOPE_WITH_DASH_PANEL.require(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.STEEP_SLOPE_WITH_DASH_PANEL.require(), RenderType.translucent());
    }
}
