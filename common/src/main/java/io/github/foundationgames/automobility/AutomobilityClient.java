package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.automobile.AutomobileData;
import io.github.foundationgames.automobility.automobile.render.AutomobileModels;
import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.automobile.render.ExhaustFumesModel;
import io.github.foundationgames.automobility.automobile.render.SkidEffectModel;
import io.github.foundationgames.automobility.automobile.render.attachment.front.HarvesterFrontAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.front.MobControllerFrontAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.BannerPostRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.BlockRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.ChestRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.GrindstoneRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.PassengerSeatRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.PlowRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.StonecutterRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.engine.CopperEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.CreativeEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.DiamondEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.GoldEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.IronEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.StoneEngineModel;
import io.github.foundationgames.automobility.automobile.render.frame.CARRFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.MotorcarFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.PineappleFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.RickshawFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.ShoppingCartFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.StandardFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.TractorFrameModel;
import io.github.foundationgames.automobility.automobile.render.item.ItemRenderableAutomobile;
import io.github.foundationgames.automobility.automobile.render.wheel.CarriageWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.OffRoadWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.StandardWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.SteelWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.TractorWheelModel;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.block.entity.render.AutomobileAssemblerBlockEntityRenderer;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.entity.render.AutomobileEntityRenderer;
import io.github.foundationgames.automobility.item.AutomobileComponentItem;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreen;
import io.github.foundationgames.automobility.screen.SingleSlotScreen;
import io.github.foundationgames.automobility.sound.AutomobileSoundInstance;
import io.github.foundationgames.automobility.util.FloatFunc;
import io.github.foundationgames.automobility.util.network.ClientPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GrassColor;

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

        Platform.get().registerMenuScreen(Automobility.AUTO_MECHANIC_SCREEN.require(), AutoMechanicTableScreen::new);
        Platform.get().registerMenuScreen(Automobility.SINGLE_SLOT_SCREEN.require(), SingleSlotScreen::new);
    }

    public static void initBlocks() {
        Platform.get().blockEntityRenderer(AutomobilityBlocks.AUTOMOBILE_ASSEMBLER_ENTITY.require(), AutomobileAssemblerBlockEntityRenderer::new);
    }

    public static void initItems() {
        var automobileReader = new AutomobileData();
        var itemAutomobile = new ItemRenderableAutomobile(automobileReader);

        Platform.get().builtinItemRenderer(AutomobilityItems.AUTOMOBILE.require(), (stack, type, pose, buffers, light, overlay) -> {
            automobileReader.read(stack.getOrCreateTagElement("Automobile"));
            float wheelDist = automobileReader.getFrame().model().lengthPx() / 16;
            float scale = 1;
            scale /= wheelDist * 0.77f;
            pose.scale(scale, scale, scale);
            AutomobileRenderer.render(pose, buffers, light, overlay, Minecraft.getInstance().getFrameTime(), itemAutomobile);
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
    }

    public static <T extends AutomobileComponent<T>> void componentItemRenderer(AutomobileComponentItem<T> item, Function<T, Model> modelProvider, Function<T, ResourceLocation> textureProvider, FloatFunc<T> scaleProvider) {
        Platform.get().builtinItemRenderer(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            var component = item.getComponent(stack);
            if (item.isVisible(component)) {
                var model = modelProvider.apply(component);
                float scale = scaleProvider.apply(component);
                matrices.translate(0.5, 0, 0.5);
                matrices.scale(scale, -scale, -scale);
                model.renderToBuffer(matrices, vertexConsumers.getBuffer(model.renderType(textureProvider.apply(component))), light, overlay, 1, 1, 1, 1);
            }
        });
    }

    public static void initEntities() {
        var libs = Platform.get();

        libs.entityRenderer(AutomobilityEntities.AUTOMOBILE.require(), AutomobileEntityRenderer::new);

        libs.modelLayer(StandardFrameModel.MODEL_LAYER);
        libs.modelLayer(TractorFrameModel.MODEL_LAYER);
        libs.modelLayer(MotorcarFrameModel.MODEL_LAYER);
        libs.modelLayer(ShoppingCartFrameModel.MODEL_LAYER);
        libs.modelLayer(CARRFrameModel.MODEL_LAYER);
        libs.modelLayer(PineappleFrameModel.MODEL_LAYER);
        libs.modelLayer(RickshawFrameModel.MODEL_LAYER);

        libs.modelLayer(StandardWheelModel.MODEL_LAYER);
        libs.modelLayer(OffRoadWheelModel.MODEL_LAYER);
        libs.modelLayer(TractorWheelModel.MODEL_LAYER);
        libs.modelLayer(CarriageWheelModel.MODEL_LAYER);
        libs.modelLayer(SteelWheelModel.MODEL_LAYER);

        libs.modelLayer(StoneEngineModel.MODEL_LAYER);
        libs.modelLayer(IronEngineModel.MODEL_LAYER);
        libs.modelLayer(CopperEngineModel.MODEL_LAYER);
        libs.modelLayer(GoldEngineModel.MODEL_LAYER);
        libs.modelLayer(DiamondEngineModel.MODEL_LAYER);
        libs.modelLayer(CreativeEngineModel.MODEL_LAYER);

        libs.modelLayer(PassengerSeatRearAttachmentModel.MODEL_LAYER);
        libs.modelLayer(BlockRearAttachmentModel.MODEL_LAYER);
        libs.modelLayer(GrindstoneRearAttachmentModel.MODEL_LAYER);
        libs.modelLayer(StonecutterRearAttachmentModel.MODEL_LAYER);
        libs.modelLayer(ChestRearAttachmentModel.MODEL_LAYER);
        libs.modelLayer(BannerPostRearAttachmentModel.MODEL_LAYER);
        libs.modelLayer(PlowRearAttachmentModel.MODEL_LAYER);

        libs.modelLayer(MobControllerFrontAttachmentModel.MODEL_LAYER);
        libs.modelLayer(HarvesterFrontAttachmentModel.MODEL_LAYER);

        libs.modelLayer(SkidEffectModel.MODEL_LAYER);
        libs.modelLayer(ExhaustFumesModel.MODEL_LAYER);

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
    }

    public static double modifyBoostFov(Minecraft client, double old, float tickDelta) {
        var player = client.player;

        if (player.getVehicle() instanceof AutomobileEntity auto) {
            return old + ((Math.sqrt(auto.getBoostSpeed(tickDelta)) * 18) * client.options.fovEffectScale().get());
        }

        return old;
    }
}
