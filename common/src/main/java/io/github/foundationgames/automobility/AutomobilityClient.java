package io.github.foundationgames.automobility;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.render.AutomobileModels;
import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import io.github.foundationgames.automobility.automobile.render.item.SimpleRenderableAutomobile;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.block.entity.render.AutomobileAssemblerBlockEntityRenderer;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.entity.render.AutomobileEntityRenderer;
import io.github.foundationgames.automobility.item.AutomobileComponentItem;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreen;
import io.github.foundationgames.automobility.screen.MenuScreenRegistrar;
import io.github.foundationgames.automobility.screen.SingleSlotScreen;
import io.github.foundationgames.automobility.sound.AutomobileSoundInstance;
import io.github.foundationgames.automobility.sound.SlicedLoopingAutomobileSoundInstance;
import io.github.foundationgames.automobility.util.FloatFunc;
import io.github.foundationgames.automobility.util.network.ClientPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.LightBlock;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class AutomobilityClient {
    public static final BlockColor GRASS_COLOR = (state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D);
    public static final ItemColor GRASS_ITEM_COLOR = (stack, tintIndex) -> GrassColor.get(0.5D, 1.0D);

    public static void init() {
        AutomobileModels.init();

        initBlocks();
        initItems();
        initEntities();
        ClientPackets.initClient();

        Platform.get().controller().initCompat();

        Platform.get().registerClientCommand((dispatcher, registries) ->
                dispatcher.register(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("automobilityc")
                        .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("dump")
                                .executes(ctx -> {
                                    if (tryDumpBuiltinResources(registries)) {
                                        sendClientMessage("Dumped all resources to .minecraft/automobility_dump/");
                                        return 0;
                                    } else {
                                        sendClientMessage("Error dumping resources! See game log for details.");
                                        return 1;
                                    }
                                })
                        )
                )
        );

        ClientLevel.MARKER_PARTICLE_ITEMS = new HashSet<>(ClientLevel.MARKER_PARTICLE_ITEMS);
        ClientLevel.MARKER_PARTICLE_ITEMS.add(AutomobilityBlocks.OFF_ROAD_AREA.require().asItem());

        //ItemProperties.register(Items.LIGHT, ResourceLocation.withDefaultNamespace("level"), (p_329788_, p_329789_, p_329790_, p_329791_) -> {
        //    BlockItemStateProperties blockitemstateproperties = p_329788_.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        //    Integer integer = blockitemstateproperties.get(LightBlock.LEVEL);
        //    return integer != null ? (float)integer.intValue() / 16.0F : 1.0F;
        //});
    }

    public static void initBlocks() {
        Platform.get().blockEntityRenderer(AutomobilityBlocks.AUTOMOBILE_ASSEMBLER_ENTITY.require(), AutomobileAssemblerBlockEntityRenderer::new);
    }

    public static void initItems() {
        Platform.get().builtinItemRenderer(AutomobilityItems.AUTOMOBILE.require(), (stack, type, pose, buffers, light, overlay) -> {
            var data = stack.get(AutomobilityItems.COMPONENT_AUTOMOBILE_DATA.require());
            if (data == null) return;

            var lvl = Minecraft.getInstance().level;
            if (lvl == null) return;

            var frame = lvl.registryAccess().registryOrThrow(AutomobileFrame.REGISTRY).get(data.frame());
            var wheel = lvl.registryAccess().registryOrThrow(AutomobileWheel.REGISTRY).get(data.wheel());
            var engine = lvl.registryAccess().registryOrThrow(AutomobileEngine.REGISTRY).get(data.engine());

            if (frame == null || wheel == null || engine == null) {
                return;
            }

            float wheelDist = frame.model().lengthPx() / 16;
            float scale = 1;
            scale /= wheelDist * 0.77f;
            pose.scale(scale, scale, scale);
            AutomobileRenderer.render(pose, buffers, light, overlay, 0f, new SimpleRenderableAutomobile(frame, engine, wheel));
        });
        componentItemRenderer(AutomobilityItems.AUTOMOBILE_FRAME.require(),
                t -> AutomobileModels.getModel(t.model().modelId()),
                t -> t.model().texture(), t -> 1 / ((t.model().lengthPx() / 16) * 0.77f)
        );
        componentItemRenderer(AutomobilityItems.AUTOMOBILE_WHEEL.require(),
                t -> AutomobileModels.getModel(t.model().modelId()),
                t -> t.model().texture(), t -> 6 / t.model().radius()
        );
        componentItemRenderer(AutomobilityItems.AUTOMOBILE_ENGINE.require(),
                t -> AutomobileModels.getModel(t.model().modelId()),
                t -> t.model().texture(), t -> 1
        );
        componentItemRenderer(AutomobilityItems.REAR_ATTACHMENT.require(),
                t -> AutomobileModels.getModel(t.model().modelId()),
                t -> t.model().texture(), t -> 1
        );
        componentItemRenderer(AutomobilityItems.FRONT_ATTACHMENT.require(),
                t -> AutomobileModels.getModel(t.model().modelId()),
                t -> t.model().texture(), t -> t.model().scale()
        );

        Platform.get().itemModelPredicate(AutomobilityBlocks.AUTOPILOT_SIGN.require().asItem(), Automobility.rl("stop"),
                (stack, lvl, user, i) -> user != null && user.isUsingItem() ? 1 : 0);
    }

    public static void initMenuScreens(MenuScreenRegistrar screens) {
        screens.accept(Automobility.AUTO_MECHANIC_SCREEN, AutoMechanicTableScreen::new);
        screens.accept(Automobility.SINGLE_SLOT_SCREEN, SingleSlotScreen::new);
    }

    public static <T extends AutomobileComponent<T>, V> void componentItemRenderer(AutomobileComponentItem<T, V> item, Function<T, Model> modelProvider, Function<T, ResourceLocation> textureProvider, FloatFunc<T> scaleProvider) {
        Platform.get().builtinItemRenderer(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            var lvl = Minecraft.getInstance().level;
            if (lvl == null) return;

            var component = item.getComponent(stack, lvl.registryAccess());
            if (item.isVisible(component)) {
                var model = modelProvider.apply(component);
                if (model == null) return;
                if (model instanceof BaseModel base) {
                    base.setDefaultState(0);
                }

                float scale = scaleProvider.apply(component);
                matrices.translate(0.5, 0, 0.5);
                matrices.scale(scale, -scale, -scale);
                model.renderToBuffer(matrices, vertexConsumers.getBuffer(model.renderType(textureProvider.apply(component))), light, overlay, 0xFFFFFFFF);

                if (model instanceof BaseModel base) {
                    base.doOtherLayerRender(matrices, vertexConsumers, light, overlay);
                }
            }
        });
    }

    public static void initEntities() {
        var libs = Platform.get();

        libs.entityRenderer(AutomobilityEntities.AUTOMOBILE.require(), AutomobileEntityRenderer::new);
        libs.entityRenderer(AutomobilityEntities.HITBOX.require(), NoopRenderer::new);

        AutomobileEntity.engineSound = auto -> {
            if (auto.getEngine().isEmpty()) {
                return;
            }

            var client = Minecraft.getInstance();
            client.getSoundManager().play(new AutomobileSoundInstance.EngineSound(client, auto));
        };
        AutomobileEntity.skidSound = auto -> {
            var client = Minecraft.getInstance();
            client.getSoundManager().play(new AutomobileSoundInstance.SkiddingSound(client, auto));
        };
        AutomobileEntity.hornSound = auto -> {
            var client = Minecraft.getInstance();
            var horn = auto.getFrame().horn();
            for (float pitch : horn.pitches()) {
                client.getSoundManager().play(new SlicedLoopingAutomobileSoundInstance.HornSound(client, auto, horn, pitch));
            }
        };
    }

    public static double modifyBoostFov(Minecraft client, double old, float tickDelta) {
        var player = client.player;

        if (player.getVehicle() instanceof AutomobileEntity auto) {
            return old + ((Math.sqrt(auto.getBoostSpeed(tickDelta)) * 18) * client.options.fovEffectScale().get());
        }

        return old;
    }

    public static boolean tryDumpBuiltinResources(HolderLookup.Provider registries) {
        try {
            AutomobileModels.dump();
            Automobility.dumpDynamicRegistries(registries);

            return true;
        } catch (IOException ex) {
            Automobility.LOG.error("Error dumping Automobility resources: ", ex);
        }
        return false;
    }

    public static void sendClientMessage(String message) {
        var mc = Minecraft.getInstance();
        var txt = Component.literal(message);
        mc.gui.getChat().addMessage(txt);
        mc.getNarrator().sayNow(txt);
    }
}
