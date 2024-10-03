package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.Automobility;
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
import io.github.foundationgames.automobility.automobile.render.wheel.CarriageWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.OffRoadWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.StandardWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.SteelWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.TractorWheelModel;
import io.github.foundationgames.automobility.entity.render.AutomobileEntityRenderer;
import io.github.foundationgames.jsonem.JsonEM;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public enum AutomobilityEntities {;
    public static final EntityType<AutomobileEntity> AUTOMOBILE = Registry.register(
            Registry.ENTITY_TYPE,
            Automobility.id("automobile"),
            FabricEntityTypeBuilder.<AutomobileEntity>create(SpawnGroup.MISC, AutomobileEntity::new).dimensions(new EntityDimensions(1f, 0.66f, true)).trackedUpdateRate(3).trackRangeChunks(10).build()
    );

    public static final TagKey<EntityType<?>> DASH_PANEL_BOOSTABLES = TagKey.of(Registry.ENTITY_TYPE_KEY, Automobility.id("dash_panel_boostables"));

    public static final DamageSource AUTOMOBILE_DAMAGE_SOURCE = new AutomobileDamageSource("automobile");

    public static void init() {
    }


    public static void initClient() {
        EntityRendererRegistry.INSTANCE.register(AUTOMOBILE, AutomobileEntityRenderer::new);

        JsonEM.registerModelLayer(StandardFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(TractorFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(MotorcarFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(ShoppingCartFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(CARRFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(PineappleFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(RickshawFrameModel.MODEL_LAYER);

        JsonEM.registerModelLayer(StandardWheelModel.MODEL_LAYER);
        JsonEM.registerModelLayer(OffRoadWheelModel.MODEL_LAYER);
        JsonEM.registerModelLayer(TractorWheelModel.MODEL_LAYER);
        JsonEM.registerModelLayer(CarriageWheelModel.MODEL_LAYER);
        JsonEM.registerModelLayer(SteelWheelModel.MODEL_LAYER);

        JsonEM.registerModelLayer(StoneEngineModel.MODEL_LAYER);
        JsonEM.registerModelLayer(IronEngineModel.MODEL_LAYER);
        JsonEM.registerModelLayer(CopperEngineModel.MODEL_LAYER);
        JsonEM.registerModelLayer(GoldEngineModel.MODEL_LAYER);
        JsonEM.registerModelLayer(DiamondEngineModel.MODEL_LAYER);
        JsonEM.registerModelLayer(CreativeEngineModel.MODEL_LAYER);

        JsonEM.registerModelLayer(PassengerSeatRearAttachmentModel.MODEL_LAYER);
        JsonEM.registerModelLayer(BlockRearAttachmentModel.MODEL_LAYER);
        JsonEM.registerModelLayer(GrindstoneRearAttachmentModel.MODEL_LAYER);
        JsonEM.registerModelLayer(StonecutterRearAttachmentModel.MODEL_LAYER);
        JsonEM.registerModelLayer(ChestRearAttachmentModel.MODEL_LAYER);
        JsonEM.registerModelLayer(BannerPostRearAttachmentModel.MODEL_LAYER);
        JsonEM.registerModelLayer(PlowRearAttachmentModel.MODEL_LAYER);

        JsonEM.registerModelLayer(MobControllerFrontAttachmentModel.MODEL_LAYER);
        JsonEM.registerModelLayer(HarvesterFrontAttachmentModel.MODEL_LAYER);

        JsonEM.registerModelLayer(SkidEffectModel.MODEL_LAYER);
        JsonEM.registerModelLayer(ExhaustFumesModel.MODEL_LAYER);
    }
}
