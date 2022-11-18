package io.github.foundationgames.automobility.forge;

import io.github.foundationgames.automobility.AutomobilityClient;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.particle.DriftSmokeParticle;
import io.github.foundationgames.automobility.screen.AutomobileHud;
import io.github.foundationgames.automobility.util.InitlessConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = InitlessConstants.AUTOMOBILITY, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AutomobilityClientForge {
    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent setup) {
        ForgePlatform.init();

        AutomobilityClient.init();

        MinecraftForge.EVENT_BUS.<RenderGuiEvent.Pre>addListener(evt -> {
            var player = Minecraft.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                AutomobileHud.render(evt.getPoseStack(), player, auto, evt.getPartialTick());
            }
        });
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent evt) {
        evt.register(AutomobilityParticles.DRIFT_SMOKE.require(), DriftSmokeParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block evt) {
        evt.register(AutomobilityBlocks.GRASS_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item evt) {
        evt.register(AutomobilityBlocks.GRASS_ITEM_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
    }
}
