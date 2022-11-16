package io.github.foundationgames.automobility.automobile.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.WheelBase;
import io.github.foundationgames.automobility.automobile.render.attachment.front.FrontAttachmentRenderModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.RearAttachmentRenderModel;
import io.github.foundationgames.automobility.automobile.render.wheel.WheelContextReceiver;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public enum AutomobileRenderer {;
    private static Model skidEffectModel;
    private static Model exhaustFumesModel;

    public static void render(
            PoseStack pose, MultiBufferSource vertexConsumers, int light, int overlay, float tickDelta,
            EntityRendererProvider.Context ctx, RenderableAutomobile automobile
    ) {
        var frame = automobile.getFrame();
        var wheels = automobile.getWheels();
        var engine = automobile.getEngine();

        if (skidEffectModel == null || exhaustFumesModel == null) {
            skidEffectModel = new SkidEffectModel(ctx);
            exhaustFumesModel = new ExhaustFumesModel(ctx);
        }

        pose.pushPose();

        pose.mulPose(Vector3f.ZP.rotationDegrees(180));
        pose.mulPose(Vector3f.YP.rotationDegrees(automobile.getAutomobileYaw(tickDelta) + 180));

        float chassisRaise = wheels.model().radius() / 16;
        float bounce = automobile.getSuspensionBounce(tickDelta) * 0.048f;

        var frameModel = automobile.getFrameModel(ctx);
        var wheelModel = automobile.getWheelModel(ctx);
        var engineModel = automobile.getEngineModel(ctx);
        var rearAttachmentModel = automobile.getRearAttachmentModel(ctx);
        var frontAttachmentModel = automobile.getFrontAttachmentModel(ctx);

        pose.translate(0, -chassisRaise, 0);

        // Frame, engine, exhaust
        pose.pushPose();

        pose.translate(0, bounce + (automobile.engineRunning() ? (Math.cos((automobile.getTime() + tickDelta) * 2.7) / 156) : 0), 0);
        var frameTexture = frame.model().texture();
        var engineTexture = engine.model().texture();
        if (!frame.isEmpty() && frameModel != null) {
            frameModel.renderToBuffer(pose, vertexConsumers.getBuffer(frameModel.renderType(frameTexture)), light, overlay, 1, 1, 1, 1);
            if (frameModel instanceof BaseModel base) {
                base.doOtherLayerRender(pose, vertexConsumers, light, overlay);
            }
        }

        float eBack = frame.model().enginePosBack() / 16;
        float eUp = frame.model().enginePosUp() / 16;
        pose.translate(0, -eUp, eBack);
        pose.mulPose(Vector3f.YP.rotationDegrees(180));
        if (!engine.isEmpty() && engineModel != null) {
            engineModel.renderToBuffer(pose, vertexConsumers.getBuffer(engineModel.renderType(engineTexture)), light, overlay, 1, 1, 1, 1);
            if (engineModel instanceof BaseModel base) {
                base.doOtherLayerRender(pose, vertexConsumers, light, overlay);
            }
        }

        VertexConsumer exhaustBuffer = null;
        ResourceLocation[] exhaustTexes;
        if (automobile.getBoostTimer() > 0) {
            exhaustTexes = ExhaustFumesModel.FLAME_TEXTURES;
            int index = (int)(automobile.getTime() % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderType.eyes(exhaustTexes[index]));
        } else if (automobile.engineRunning()) {
            exhaustTexes = ExhaustFumesModel.SMOKE_TEXTURES;
            int index = (int)Math.floor(((automobile.getTime() + tickDelta) / 1.5f) % exhaustTexes.length);
            exhaustBuffer = vertexConsumers.getBuffer(RenderType.entityTranslucent(exhaustTexes[index]));
        }
        if (exhaustBuffer != null) {
            for (AutomobileEngine.ExhaustPos exhaust : engine.model().exhausts()) {
                pose.pushPose();

                pose.translate(exhaust.x() / 16, -exhaust.y() / 16, exhaust.z() / 16);
                pose.mulPose(Vector3f.YP.rotationDegrees(exhaust.yaw()));
                pose.mulPose(Vector3f.XP.rotationDegrees(exhaust.pitch()));
                exhaustFumesModel.renderToBuffer(pose, exhaustBuffer, light, overlay, 1, 1, 1, 1);

                pose.popPose();
            }
        }
        pose.popPose();

        // Rear Attachment
        var rearAtt = automobile.getRearAttachmentType();
        if (!rearAtt.isEmpty()) {
            pose.pushPose();
            pose.translate(0, chassisRaise, frame.model().rearAttachmentPos() / 16);
            pose.mulPose(Vector3f.YN.rotationDegrees(automobile.getAutomobileYaw(tickDelta) - automobile.getRearAttachmentYaw(tickDelta)));

            pose.translate(0, 0, rearAtt.model().pivotDistPx() / 16);
            if (rearAttachmentModel instanceof RearAttachmentRenderModel rm) {
                rm.setRenderState(automobile.getRearAttachment(), (float) Math.toRadians(automobile.getWheelAngle(tickDelta)), tickDelta);
            }
            rearAttachmentModel.renderToBuffer(pose, vertexConsumers.getBuffer(rearAttachmentModel.renderType(rearAtt.model().texture())), light, overlay, 1, 1, 1, 1);
            if (rearAttachmentModel instanceof BaseModel base) {
                base.doOtherLayerRender(pose, vertexConsumers, light, overlay);
            }
            pose.popPose();
        }

        // Front Attachment
        var frontAtt = automobile.getFrontAttachmentType();
        if (!frontAtt.isEmpty()) {
            pose.pushPose();
            pose.translate(0, 0, frame.model().frontAttachmentPos() / -16);

            if (frontAttachmentModel instanceof FrontAttachmentRenderModel fm) {
                fm.setRenderState(automobile.getFrontAttachment(), chassisRaise, tickDelta);
            }
            frontAttachmentModel.renderToBuffer(pose, vertexConsumers.getBuffer(frontAttachmentModel.renderType(frontAtt.model().texture())), light, overlay, 1, 1, 1, 1);
            if (frontAttachmentModel instanceof BaseModel base) {
                base.doOtherLayerRender(pose, vertexConsumers, light, overlay);
            }
            pose.popPose();
        }

        // WHEELS ----------------------------------------
        var wPoses = frame.model().wheelBase().wheels;

        if (!wheels.isEmpty()) {
            var wheelBuffer = vertexConsumers.getBuffer(wheelModel.renderType(wheels.model().texture()));
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
                pose.pushPose();

                pose.translate(pos.right() / 16, wheelRadius / 16, -pos.forward() / 16);

                if (pos.end() == WheelBase.WheelEnd.FRONT) pose.mulPose(Vector3f.YP.rotationDegrees(automobile.getSteering(tickDelta) * 27));
                pose.translate(0, -chassisRaise, 0);
                pose.mulPose(Vector3f.XP.rotationDegrees(wheelAngle));
                pose.scale(scale, scale, scale);

                pose.mulPose(Vector3f.YP.rotationDegrees(180 + pos.yaw()));

                wheelModel.renderToBuffer(pose, wheelBuffer, light, overlay, 1, 1, 1, 1);
                if (wheelModel instanceof BaseModel base) {
                    base.doOtherLayerRender(pose, vertexConsumers, light, overlay);
                }

                pose.popPose();

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
                r = c.x() * 0.85f;
                g = c.y() * 0.85f;
                b = c.z() * 0.85f;
                bright = false;
            }
            int index = (int)Math.floor(((automobile.getTime() + tickDelta) / 1.5f) % skidTexes.length);
            var skidEffectBuffer = vertexConsumers.getBuffer(bright ? RenderType.eyes(skidTexes[index]) : RenderType.entitySmoothCutout(skidTexes[index]));

            for (var pos : wPoses) {
                if (pos.end() == WheelBase.WheelEnd.BACK) {
                    float scale = pos.scale();
                    float heightOffset = wheels.model().radius();
                    float wheelRadius = wheels.model().radius() * scale;
                    float wheelWidth =  (wheels.model().width() / 16) * scale;
                    float back = (wheelRadius > 2) ? (float) (Math.sqrt((wheelRadius * wheelRadius) - Math.pow(wheelRadius - 2, 2)) - 0.85) / 16 : 0.08f;
                    pose.pushPose();
                    pose.translate((pos.right() / 16) + (wheelWidth * (pos.side() == WheelBase.WheelSide.RIGHT ? 1 : -1)), heightOffset / 16, (-pos.forward() / 16) + back);
                    pose.scale(pos.side() == WheelBase.WheelSide.LEFT ? -1 : 1, 1, -1);
                    skidEffectModel.renderToBuffer(pose, skidEffectBuffer, light, overlay, r, g, b, 0.6f);
                    pose.popPose();
                }
            }
        }
        // -----------------------------------------------

        pose.popPose();
    }
}
