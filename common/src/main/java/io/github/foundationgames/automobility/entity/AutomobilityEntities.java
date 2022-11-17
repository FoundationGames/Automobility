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
import io.github.foundationgames.automobility.intermediary.Intermediary;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum AutomobilityEntities {;
    public static final EntityType<AutomobileEntity> AUTOMOBILE = RegistryQueue.register(Registry.ENTITY_TYPE,
            Automobility.rl("automobile"),
            Intermediary.get().entityType(MobCategory.MISC, AutomobileEntity::new, new EntityDimensions(1f, 0.66f, true), 3, 10)
    );

    public static final TagKey<EntityType<?>> DASH_PANEL_BOOSTABLES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, Automobility.rl("dash_panel_boostables"));

    public static final DamageSource AUTOMOBILE_DAMAGE_SOURCE = new AutomobileDamageSource("automobile");

    public static void init() {
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        var libs = Intermediary.get();

        libs.entityRenderer(AUTOMOBILE, AutomobileEntityRenderer::new);

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
    }
}
