package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.ExhaustFumesModel;
import io.github.foundationgames.automobility.automobile.render.SkidEffectModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.PassengerSeatRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.engine.CopperEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.CreativeEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.DiamondEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.GoldEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.IronEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.StoneEngineModel;
import io.github.foundationgames.automobility.automobile.render.frame.*;
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
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
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
            FabricEntityTypeBuilder.<AutomobileEntity>create(SpawnGroup.MISC, AutomobileEntity::new).dimensions(new EntityDimensions(1f, 0.66f, true)).trackRangeChunks(10).build()
    );

    public static final TagKey<EntityType<?>> DASH_PANEL_BOOSTABLES = TagKey.of(Registry.ENTITY_TYPE_KEY, Automobility.id("dash_panel_boostables"));

    public static final DamageSource AUTOMOBILE_DAMAGE_SOURCE = new AutomobileDamageSource("automobile");

    public static void init() {
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        EntityRendererRegistry.INSTANCE.register(AUTOMOBILE, AutomobileEntityRenderer::new);

        JsonEM.registerModelLayer(StandardFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(TractorFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(MotorcarFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(ShoppingCartFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(CARRFrameModel.MODEL_LAYER);
        JsonEM.registerModelLayer(PineappleFrameModel.MODEL_LAYER);

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

        JsonEM.registerModelLayer(SkidEffectModel.MODEL_LAYER);
        JsonEM.registerModelLayer(ExhaustFumesModel.MODEL_LAYER);
    }
}
