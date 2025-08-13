package io.github.foundationgames.automobility.neoforge;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.AutomobilityClient;
import io.github.foundationgames.automobility.automobile.render.AutomobileModels;
import io.github.foundationgames.automobility.automobile.render.obj.ObjLoader;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.block.OffroadAreaBlock;
import io.github.foundationgames.automobility.block.model.SlopeBakedModel;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.neoforge.block.render.NeoForgeSlopeBakedModel;
import io.github.foundationgames.automobility.neoforge.block.render.NeoForgeSlopeGeometryLoader;
import io.github.foundationgames.automobility.neoforge.block.render.SlopeModelsProvider;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.particle.DriftSmokeParticle;
import io.github.foundationgames.automobility.screen.AutomobileHud;
import io.github.foundationgames.automobility.screen.MenuScreenRegistrar;
import io.github.foundationgames.automobility.util.Eventual;
import io.github.foundationgames.automobility.util.InitlessConstants;
import io.github.foundationgames.automobility.util.TriFunc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.LightBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = InitlessConstants.AUTOMOBILITY, bus = EventBusSubscriber.Bus.MOD)
public class AutomobilityClientNeoForge {
    public static final AutomobileModels MODEL_DEF_LOADER = new AutomobileModels();

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent setup) {
        NeoForgePlatform.init();

        AutomobilityClient.init();

        SlopeBakedModel.impl = NeoForgeSlopeBakedModel::new;

        NeoForge.EVENT_BUS.<RenderGuiEvent.Pre>addListener(evt -> {
            var player = Minecraft.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                AutomobileHud.render(evt.getGuiGraphics(), player, auto, evt.getPartialTick().getGameTimeDeltaTicks());
            }
        });

        NeoForge.EVENT_BUS.<ViewportEvent.ComputeFov>addListener(evt ->
                evt.setFOV(AutomobilityClient.modifyBoostFov(Minecraft.getInstance(), evt.getFOV(), (float) evt.getPartialTick())));
    }

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent evt) {
        evt.enqueueWork(() ->
                ItemProperties.register(AutomobilityBlocks.OFF_ROAD_AREA.require().asItem(),
                        ResourceLocation.fromNamespaceAndPath(Automobility.MOD_ID, "strength"), (stack, level, living, id) -> {
                            BlockItemStateProperties blockitemstateproperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
                            Integer integer = blockitemstateproperties.get(OffroadAreaBlock.STRENGTH);
                            return integer != null ? (float)integer/OffroadAreaBlock.MAX_STRENGTH : 1;
                }));
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent evt) {
        evt.registerSpriteSet(AutomobilityParticles.DRIFT_SMOKE.require(), DriftSmokeParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block evt) {
        evt.register(AutomobilityClient.GRASS_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item evt) {
        evt.register(AutomobilityClient.GRASS_ITEM_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent evt) {
        AutomobilityClient.initMenuScreens(new MenuScreenRegistrar() {
            @Override
            public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void accept(
                    Eventual<MenuType<T>> type, TriFunc<T, Inventory, Component, U> factory) {
                evt.register(type.require(), factory::apply);
            }
        });
    }

    @SubscribeEvent
    public static void registerResourceLoaders(RegisterClientReloadListenersEvent evt) {
        evt.registerReloadListener(MODEL_DEF_LOADER);
        evt.registerReloadListener(ObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerBakedModels(ModelEvent.RegisterGeometryLoaders evt) {
        evt.register(NeoForgeSlopeGeometryLoader.ID, NeoForgeSlopeGeometryLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void generateResources(GatherDataEvent evt) {
        var generator = evt.getGenerator();
        var output = generator.getPackOutput();
        var files = evt.getExistingFileHelper();

        generator.addProvider(evt.includeClient(),
                new SlopeModelsProvider(output, files));
    }
}
