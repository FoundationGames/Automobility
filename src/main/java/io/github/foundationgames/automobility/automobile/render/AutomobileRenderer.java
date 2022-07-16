package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.WheelBase;
import io.github.foundationgames.automobility.automobile.render.attachment.front.FrontAttachmentRenderModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.RearAttachmentRenderModel;
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
            EntityRendererFactory.Context ctx, RenderableAutomobile automobile
    ) {
        var frame = automobile.getFrame();
        var wheels = automobile.getWheels();
        var engine = automobile.getEngine();

        if (skidEffectModel == null || exhaustFumesModel == null) {
            skidEffectModel = new SkidEffectModel(ctx);
            exhaustFumesModel = new ExhaustFumesModel(ctx);
        }

        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(automobile.getAutomobileYaw(tickDelta) + 180));

        float chassisRaise = wheels.model().radius() / 16;
        float bounce = automobile.getSuspensionBounce(tickDelta) * 0.048f;

        var frameModel = automobile.getFrameModel(ctx);
        var wheelModel = automobile.getWheelModel(ctx);
        var engineModel = automobile.getEngineModel(ctx);
        var rearAttachmentModel = automobile.getRearAttachmentModel(ctx);
        var frontAttachmentModel = automobile.getFrontAttachmentModel(ctx);

        matrices.translate(0, -chassisRaise, 0);

        // Frame, engine, exhaust
        matrices.push();

        matrices.translate(0, bounce + (automobile.engineRunning() ? (Math.cos((automobile.getTime() + tickDelta) * 2.7) / 156) : 0), 0);
        var frameTexture = frame.model().texture();
        var engineTexture = engine.model().texture();
        if (!frame.isEmpty() && frameModel != null) frameModel.render(matrices, vertexConsumers.getBuffer(frameModel.getLayer(frameTexture)), light, overlay, 1, 1, 1, 1);

        float eBack = frame.model().enginePosBack() / 16;
        float eUp = frame.model().enginePosUp() / 16;
        matrices.translate(0, -eUp, eBack);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        if (!engine.isEmpty() && engineModel != null) engineModel.render(matrices, vertexConsumers.getBuffer(engineModel.getLayer(engineTexture)), light, overlay, 1, 1, 1, 1);

        VertexConsumer exhaustBuffer = null;
        Identifier[] exhaustTexes;
        if (automobile.getBoostTimer() > 0) {
            exhaustTexes = ExhaustFumesModel.FLAME_TEXTURES;
            int index = (int)(automobile.getTime() % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderLayer.getEyes(exhaustTexes[index]));
        } else if (automobile.engineRunning()) {
            exhaustTexes = ExhaustFumesModel.SMOKE_TEXTURES;
            int index = (int)Math.floor(((automobile.getTime() + tickDelta) / 1.5f) % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(exhaustTexes[index]));
        }
        if (exhaustBuffer != null) {
            for (AutomobileEngine.ExhaustPos exhaust : engine.model().exhausts()) {
                matrices.push();

                matrices.translate(exhaust.x() / 16, -exhaust.y() / 16, exhaust.z() / 16);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(exhaust.yaw()));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(exhaust.pitch()));
                exhaustFumesModel.render(matrices, exhaustBuffer, light, overlay, 1, 1, 1, 1);

                matrices.pop();
            }
        }
        matrices.pop();

        // Rear Attachment
        var rearAtt = automobile.getRearAttachmentType();
        if (!rearAtt.isEmpty()) {
            matrices.push();
            matrices.translate(0, chassisRaise, frame.model().rearAttachmentPos() / 16);
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(automobile.getAutomobileYaw(tickDelta) - automobile.getRearAttachmentYaw(tickDelta)));

            matrices.translate(0, 0, rearAtt.model().pivotDistPx() / 16);
            if (rearAttachmentModel instanceof RearAttachmentRenderModel rm) {
                rm.setRenderState(automobile.getRearAttachment(), (float) Math.toRadians(automobile.getWheelAngle(tickDelta)), tickDelta);
            }
            rearAttachmentModel.render(matrices, vertexConsumers.getBuffer(rearAttachmentModel.getLayer(rearAtt.model().texture())), light, overlay, 1, 1, 1, 1);
            matrices.pop();
        }

        // Front Attachment
        var frontAtt = automobile.getFrontAttachmentType();
        if (!frontAtt.isEmpty()) {
            matrices.push();
            matrices.translate(0, 0, frame.model().frontAttachmentPos() / -16);

            if (frontAttachmentModel instanceof FrontAttachmentRenderModel fm) {
                fm.setRenderState(automobile.getFrontAttachment(), chassisRaise, tickDelta);
            }
            frontAttachmentModel.render(matrices, vertexConsumers.getBuffer(frontAttachmentModel.getLayer(frontAtt.model().texture())), light, overlay, 1, 1, 1, 1);
            matrices.pop();
        }

        // WHEELS ----------------------------------------
        var wPoses = frame.model().wheelBase().wheels;

        if (!wheels.isEmpty()) {
            var wheelBuffer = vertexConsumers.getBuffer(wheelModel.getLayer(wheels.model().texture()));
            float wheelAngle = automobile.getWheelAngle(tickDelta);
            int wheelCount = automobile.getWheelCount();

            for (var pos : wPoses) {
                if (wheelCount <= 0) {
                    break;
                }

                if (wheelModel instanceof WheelContextReceiver receiver) {
                    receiver.provideContext(pos);
                }
                float scale = pos.scale();
                float wheelRadius = wheels.model().radius() - (wheels.model().radius() * (scale - 1));
                matrices.push();

                matrices.translate(pos.right() / 16, wheelRadius / 16, -pos.forward() / 16);

                if (pos.end() == WheelBase.WheelEnd.FRONT) matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(automobile.getSteering(tickDelta) * 27));
                matrices.translate(0, -chassisRaise, 0);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(wheelAngle));
                matrices.scale(scale, scale, scale);

                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180 + pos.yaw()));

                wheelModel.render(matrices, wheelBuffer, light, overlay, 1, 1, 1, 1);

                matrices.pop();

                wheelCount--;
            }
        }

        // Skid effects
        if ((automobile.getTurboCharge() > AutomobileEntity.SMALL_TURBO_TIME || automobile.debris()) && automobile.automobileOnGround()) {
            var skidTexes = SkidEffectModel.COOL_SPARK_TEXTURES;
            boolean bright = true;
            float r = 1;
            float g = 1;
            float b = 1;
            if (automobile.getTurboCharge() > AutomobileEntity.LARGE_TURBO_TIME) {
                skidTexes = SkidEffectModel.FLAME_TEXTURES;
            } else if (automobile.getTurboCharge() > AutomobileEntity.MEDIUM_TURBO_TIME) {
                skidTexes = SkidEffectModel.HOT_SPARK_TEXTURES;
            } else if (automobile.debris()) {
                skidTexes = SkidEffectModel.DEBRIS_TEXTURES;
                var c = automobile.debrisColor();
                r = c.getX() * 0.85f;
                g = c.getY() * 0.85f;
                b = c.getZ() * 0.85f;
                bright = false;
            }
            int index = (int)Math.floor(((automobile.getTime() + tickDelta) / 1.5f) % skidTexes.length);
            var skidEffectBuffer = vertexConsumers.getBuffer(bright ? RenderLayer.getEyes(skidTexes[index]) : RenderLayer.getEntitySmoothCutout(skidTexes[index]));

            for (var pos : wPoses) {
                if (pos.end() == WheelBase.WheelEnd.BACK) {
                    float scale = pos.scale();
                    float heightOffset = wheels.model().radius();
                    float wheelRadius = wheels.model().radius() * scale;
                    float wheelWidth =  (wheels.model().width() / 16) * scale;
                    float back = (wheelRadius > 2) ? (float) (Math.sqrt((wheelRadius * wheelRadius) - Math.pow(wheelRadius - 2, 2)) - 0.85) / 16 : 0.08f;
                    matrices.push();
                    matrices.translate((pos.right() / 16) + (wheelWidth * (pos.side() == WheelBase.WheelSide.RIGHT ? 1 : -1)), heightOffset / 16, (-pos.forward() / 16) + back);
                    matrices.scale(pos.side() == WheelBase.WheelSide.LEFT ? -1 : 1, 1, -1);
                    skidEffectModel.render(matrices, skidEffectBuffer, light, overlay, r, g, b, 0.6f);
                    matrices.pop();
                }
            }
        }
        // -----------------------------------------------

        matrices.pop();
    }
}
