package io.github.foundationgames.automobility.fabric;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.AutomobilityClient;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.block.OffroadAreaBlock;
import io.github.foundationgames.automobility.block.model.SlopeBakedModel;
import io.github.foundationgames.automobility.block.model.SlopeUnbakedModel;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.fabric.block.render.FabricSlopeBakedModel;
import io.github.foundationgames.automobility.fabric.resource.FabricAutomobileModels;
import io.github.foundationgames.automobility.fabric.resource.FabricObjLoader;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.particle.DriftSmokeParticle;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.screen.AutomobileHud;
import io.github.foundationgames.automobility.screen.MenuScreenRegistrar;
import io.github.foundationgames.automobility.util.Eventual;
import io.github.foundationgames.automobility.util.TriFunc;
import io.github.foundationgames.automobility.util.network.AutomobilityPacketPayload;
import io.github.foundationgames.automobility.util.network.ClientPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlockItemStateProperties;

public class AutomobilityClientFabric implements ClientModInitializer {
    private static boolean wasRidingAutomobile = false;

    @Override
    public void onInitializeClient() {
        FabricPlatform.init();

        AutomobilityClient.init();

        AutomobilityClient.initMenuScreens(new MenuScreenRegistrar() {
            @Override
            public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void accept(
                    Eventual<MenuType<T>> type, TriFunc<T, Inventory, Component, U> factory) {
                MenuScreens.register(type.require(), factory::apply);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(AutomobilityPacketPayload.TYPE, (payload, context) ->
                ClientPackets.CLIENTBOUND_HANDLERS.getOrDefault(payload.id(), (x,y)->{}).accept(context.client(), payload.buf()));

        ParticleFactoryRegistry.getInstance().register(AutomobilityParticles.DRIFT_SMOKE.require(), DriftSmokeParticle.Factory::new);
        HudRenderCallback.EVENT.register((pose, tickDelta) -> {
            var player = Minecraft.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                AutomobileHud.render(pose, player, auto, tickDelta.getGameTimeDeltaTicks());
            }
        });

        ColorProviderRegistry.BLOCK.register(AutomobilityClient.GRASS_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());
        ColorProviderRegistry.ITEM.register(AutomobilityClient.GRASS_ITEM_COLOR, AutomobilityBlocks.GRASS_OFF_ROAD.require());

        SlopeBakedModel.impl = FabricSlopeBakedModel::new;

        ModelLoadingPlugin.register(ctx -> ctx.resolveModel().register(
                c -> SlopeUnbakedModel.DEFAULT_MODELS.getOrDefault(c.id(), () -> null).get()
        ));

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
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.AUTOMOBILE_PRESSURE_PLATE.require(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.SLOPE.require(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.STEEP_SLOPE.require(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.SLOPE_WITH_DASH_PANEL.require(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AutomobilityBlocks.STEEP_SLOPE_WITH_DASH_PANEL.require(), RenderType.translucent());

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FabricAutomobileModels.INSTANCE);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FabricObjLoader.INSTANCE);

        ItemProperties.register(AutomobilityBlocks.OFF_ROAD_AREA.require().asItem(),
                ResourceLocation.fromNamespaceAndPath(Automobility.MOD_ID, "strength"), (stack, level, living, id) -> {
                    BlockItemStateProperties blockitemstateproperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
                    Integer integer = blockitemstateproperties.get(OffroadAreaBlock.STRENGTH);
                    return integer != null ? (float)integer/OffroadAreaBlock.MAX_STRENGTH : 1;
        });
    }
}
