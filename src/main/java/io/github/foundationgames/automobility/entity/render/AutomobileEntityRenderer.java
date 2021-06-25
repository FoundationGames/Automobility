package io.github.foundationgames.automobility.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.render.ExhaustFumesModel;
import io.github.foundationgames.automobility.automobile.render.SkidEffectModel;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class AutomobileEntityRenderer extends EntityRenderer<AutomobileEntity> {
    private final Model skidEffectModel;
    private final Model exhaustFumesModel;

    private final EntityRendererFactory.Context ctx;

    public AutomobileEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.ctx = ctx;
        this.skidEffectModel = new SkidEffectModel(ctx);
        this.exhaustFumesModel = new ExhaustFumesModel(ctx);
    }

    @Override
    public Identifier getTexture(AutomobileEntity entity) {
        return null;
    }

    @Override
    public void render(AutomobileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getYaw(tickDelta) + 90));

        var wheels = entity.getWheels();
        var frame = entity.getFrame();
        var engine = entity.getEngine();
        float chassisRaise = wheels.model().radius() / 16;

        matrices.translate(0, -1.5f - chassisRaise, 0);

        float raise = 1.5f - chassisRaise;
        matrices.translate(0, raise, 0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(entity.getVerticalTravelPitch(tickDelta)));
        matrices.translate(0, -raise, 0);

        var frameModel = entity.getFrameModel(ctx);
        var wheelModel = entity.getWheelModel(ctx);
        var engineModel = entity.getEngineModel(ctx);

        // Frame and engine
        matrices.push();
        if (entity.hasPassengers()) {
            matrices.translate(0, Math.cos((entity.world.getTime() + tickDelta) * 2.7) / 156, 0);
        }
        var frameTexture = frame.model().texture();
        var engineTexture = engine.model().texture();
        if (frameModel != null) frameModel.render(matrices, vertexConsumers.getBuffer(frameModel.getLayer(frameTexture)), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        float eBack = frame.model().enginePosBack() / 16;
        float eUp = frame.model().enginePosUp() / 16;
        matrices.translate(eBack, -eUp, 0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
        if (engineModel != null) engineModel.render(matrices, vertexConsumers.getBuffer(engineModel.getLayer(engineTexture)), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();

        // Exhaust fumes
        matrices.push();
        VertexConsumer exhaustBuffer = null;
        Identifier[] exhaustTexes;
        if (entity.getBoostTimer() > 0) {
            exhaustTexes = ExhaustFumesModel.FLAME_TEXTURES;
            int index = (int)(entity.world.getTime() % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderLayer.getEyes(exhaustTexes[index]));
        } else if (entity.hasPassengers()) {
            exhaustTexes = ExhaustFumesModel.SMOKE_TEXTURES;
            int index = (int)Math.floor(((entity.world.getTime() + tickDelta) / 1.5f) % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(exhaustTexes[index]));
        }
        if (exhaustBuffer != null) {
            for (AutomobileEngine.ExhaustPos exhaust : engine.model().exhausts()) {
                matrices.push();
                matrices.translate((-exhaust.z() / 16) + eBack, (-exhaust.y() / 16) - eUp + 1.5, exhaust.x() / 16);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(exhaust.pitch()));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(exhaust.yaw()));
                matrices.translate(0, -1.5, 0);
                exhaustFumesModel.render(matrices, exhaustBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                matrices.pop();
            }
        }
        matrices.pop();

        var wheelBuffer = vertexConsumers.getBuffer(wheelModel.getLayer(entity.getWheels().model().texture()));
        float sLong = frame.model().wheelSeparationLong() / 16;
        float sWide = frame.model().wheelSeparationWide() / 16;

        // WHEELS ----------------------------------------

        float wheelAngle = entity.getWheelAngle(tickDelta);
        float wheelRadius = wheels.model().radius();

        if (wheelModel != null) {
            // Front left
            matrices.push();
            matrices.translate(-(sLong / 2), wheelRadius / 16, -(sWide / 2));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getSteering(tickDelta) * 27));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();

            // Rear left
            matrices.push();
            matrices.translate(sLong / 2, wheelRadius / 16, -(sWide / 2));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            if (entity.getDriftTimer() > 0) {
                matrices.translate(0, 0, -(wheels.model().width() / 16));

            }
            matrices.pop();

            // Rear right
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            matrices.translate(-(sLong / 2), wheelRadius / 16, -(sWide / 2));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();

            // Front right
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            matrices.translate(sLong / 2, wheelRadius / 16, -(sWide / 2));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getSteering(tickDelta) * 27));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();
        }

        // Skid effects
        if (entity.getDriftTimer() > 0) {
            var skidTexes = SkidEffectModel.SMOKE_TEXTURES;
            boolean bright = false;
            if (entity.getDriftTimer() > AutomobileEntity.DRIFT_TURBO_TIME) {
                skidTexes = SkidEffectModel.FLAME_TEXTURES;
                bright = true;
            } else if (entity.getDriftTimer() > AutomobileEntity.DRIFT_TURBO_TIME - 20) {
                skidTexes = SkidEffectModel.SPARK_TEXTURES;
            }
            int index = (int)Math.floor(((entity.world.getTime() + tickDelta) / 1.5f) % skidTexes.length);
            var skidEffectBuffer = vertexConsumers.getBuffer(bright ? RenderLayer.getEyes(skidTexes[index]) : RenderLayer.getEntityTranslucent(skidTexes[index]));

            matrices.push();
            float back = (wheelRadius / 16) - Math.max(0, ((wheelRadius / 16) - (3f / 16)) * 0.75f);
            matrices.translate((sLong / 2) + back, wheelRadius / 16, -((sWide / 2) + (wheels.model().width() / 16)));
            skidEffectModel.render(matrices, skidEffectBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 0.6f);
            matrices.pop();
            matrices.push();
            matrices.scale(1, 1, -1);
            matrices.translate((sLong / 2) + back, wheelRadius / 16, -((sWide / 2) + (wheels.model().width() / 16)));
            skidEffectModel.render(matrices, skidEffectBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 0.6f);
            matrices.pop();
        }

        // -----------------------------------------------

        matrices.pop();
    }
}
