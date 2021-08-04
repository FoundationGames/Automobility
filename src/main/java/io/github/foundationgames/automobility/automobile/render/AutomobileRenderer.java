package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.WheelBase;
import io.github.foundationgames.automobility.automobile.render.wheel.WheelContextReceiver;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public enum AutomobileRenderer {;
    private static Model skidEffectModel;
    private static Model exhaustFumesModel;

    public static void render(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float tickDelta,
            AutomobileFrame frame, AutomobileWheel wheels, AutomobileEngine engine,
            EntityRendererFactory.Context ctx, RenderableAutomobile automobile
    ) {
        if (skidEffectModel == null || exhaustFumesModel == null) {
            skidEffectModel = new SkidEffectModel(ctx);
            exhaustFumesModel = new ExhaustFumesModel(ctx);
        }

        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(automobile.getAutomobileYaw(tickDelta) + 90));

        float chassisRaise = wheels.model().radius() / 16;

        matrices.translate(0, -1.5f - chassisRaise, 0);

        float raise = 1.5f - chassisRaise;
        float bounce = automobile.getSuspensionBounce(tickDelta) * 0.048f;
        matrices.translate(0, raise, 0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(automobile.getVerticalTravelPitch(tickDelta)));
        matrices.translate(0, -raise, 0);

        var frameModel = automobile.getFrameModel(ctx);
        var wheelModel = automobile.getWheelModel(ctx);
        var engineModel = automobile.getEngineModel(ctx);

        // Frame and engine
        matrices.push();
        if (automobile.engineRunning()) {
            matrices.translate(0, (Math.cos((automobile.getWorldTime() + tickDelta) * 2.7) / 156) + bounce, 0);
        }
        var frameTexture = frame.model().texture();
        var engineTexture = engine.model().texture();
        if (frameModel != null) frameModel.render(matrices, vertexConsumers.getBuffer(frameModel.getLayer(frameTexture)), light, overlay, 1, 1, 1, 1);
        float eBack = frame.model().enginePosBack() / 16;
        float eUp = frame.model().enginePosUp() / 16;
        matrices.translate(eBack, -eUp, 0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
        if (engineModel != null) engineModel.render(matrices, vertexConsumers.getBuffer(engineModel.getLayer(engineTexture)), light, overlay, 1, 1, 1, 1);
        matrices.pop();

        // Exhaust fumes
        matrices.push();
        VertexConsumer exhaustBuffer = null;
        Identifier[] exhaustTexes;
        if (automobile.getBoostTimer() > 0) {
            exhaustTexes = ExhaustFumesModel.FLAME_TEXTURES;
            int index = (int)(automobile.getWorldTime() % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderLayer.getEyes(exhaustTexes[index]));
        } else if (automobile.engineRunning()) {
            exhaustTexes = ExhaustFumesModel.SMOKE_TEXTURES;
            int index = (int)Math.floor(((automobile.getWorldTime() + tickDelta) / 1.5f) % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(exhaustTexes[index]));
        }
        matrices.pop();
        if (exhaustBuffer != null) {
            for (AutomobileEngine.ExhaustPos exhaust : engine.model().exhausts()) {
                matrices.push();
                matrices.translate((-exhaust.z() / 16) + eBack, (-exhaust.y() / 16) - eUp + (1.5 + bounce), exhaust.x() / 16);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(exhaust.pitch()));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(exhaust.yaw()));
                matrices.translate(0, -1.5, 0);
                exhaustFumesModel.render(matrices, exhaustBuffer, light, overlay, 1, 1, 1, 1);
                matrices.pop();
            }
        }

        var wheelBuffer = vertexConsumers.getBuffer(wheelModel.getLayer(wheels.model().texture()));

        // WHEELS ----------------------------------------

        float wheelAngle = automobile.getWheelAngle(tickDelta);
        var wPoses = frame.model().wheelBase().wheels;

        if (wheelModel != null) {
            for (var pos : wPoses) {
                if (wheelModel instanceof WheelContextReceiver receiver) {
                    receiver.provideContext(pos);
                }
                float scale = pos.scale();
                float wheelRadius = wheels.model().radius() - (wheels.model().radius() * (scale - 1));
                matrices.push();
                matrices.translate(-pos.forward() / 16, wheelRadius / 16, pos.right() / 16);
                if (pos.end() == WheelBase.WheelEnd.FRONT) matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(automobile.getSteering(tickDelta) * 27));
                matrices.translate(0, raise, 0);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-wheelAngle));
                matrices.scale(scale, scale, scale);
                matrices.translate(0, -raise, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(pos.yaw()));
                wheelModel.render(matrices, wheelBuffer, light, overlay, 1, 1, 1, 1);
                matrices.pop();
            }
        }

        // Skid effects
        if ((automobile.getDriftTimer() > 0 || automobile.debris()) && automobile.automobileOnGround()) {
            var skidTexes = SkidEffectModel.SMOKE_TEXTURES;
            boolean bright = false;
            float r = 1;
            float g = 1;
            float b = 1;
            if (automobile.getDriftTimer() > AutomobileEntity.DRIFT_TURBO_TIME) {
                skidTexes = SkidEffectModel.FLAME_TEXTURES;
                bright = true;
            } else if (automobile.debris()) {
                skidTexes = SkidEffectModel.DEBRIS_TEXTURES;
                var c = automobile.debrisColor();
                r = c.getX() * 0.85f;
                g = c.getY() * 0.85f;
                b = c.getZ() * 0.85f;
            } else if (automobile.getDriftTimer() > AutomobileEntity.DRIFT_TURBO_TIME - 20) {
                skidTexes = SkidEffectModel.SPARK_TEXTURES;
            }
            int index = (int)Math.floor(((automobile.getWorldTime() + tickDelta) / 1.5f) % skidTexes.length);
            var skidEffectBuffer = vertexConsumers.getBuffer(bright ? RenderLayer.getEyes(skidTexes[index]) : RenderLayer.getEntityCutout(skidTexes[index]));

            for (var pos : wPoses) {
                if (pos.end() == WheelBase.WheelEnd.BACK) {
                    float scale = pos.scale();
                    float heightOffset = wheels.model().radius();
                    float wheelRadius = wheels.model().radius() * scale;
                    float wheelWidth =  (wheels.model().width() / 16) * scale;
                    float back = (wheelRadius / 16) - Math.max(0, ((wheelRadius / 16) - (3f / 16)) * 0.45f);
                    matrices.push();
                    matrices.translate((-pos.forward() / 16) + back, heightOffset / 16, (pos.right() / 16) + (wheelWidth * (pos.side() == WheelBase.WheelSide.RIGHT ? 1 : -1)));
                    matrices.scale(1, 1, pos.side() == WheelBase.WheelSide.RIGHT ? -1 : 1);
                    skidEffectModel.render(matrices, skidEffectBuffer, light, overlay, r, g, b, 0.6f);
                    matrices.pop();
                }
            }
        }

        // -----------------------------------------------

        matrices.pop();
    }
}
