package io.github.foundationgames.automobility.entity.render;

import io.github.foundationgames.automobility.automobile.render.SkidEffectModel;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class AutomobileEntityRenderer extends EntityRenderer<AutomobileEntity> {
    private Model frameModel;
    private Model wheelModel;
    private final Model skidEffectModel;

    private final EntityRendererFactory.Context ctx;

    public AutomobileEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.ctx = ctx;
        this.skidEffectModel = new SkidEffectModel(ctx);
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
        float chassisRaise = wheels.model().radius() / 16;

        matrices.translate(0, -1.5f - chassisRaise, 0);

        float raise = 1.5f - chassisRaise;
        matrices.translate(0, raise, 0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(entity.getVerticalTravelPitch(tickDelta)));
        matrices.translate(0, -raise, 0);

        matrices.push();
        if (entity.updateModels) {
            updateModels(entity);
        }
        if (entity.hasPassengers()) {
            matrices.translate(0, Math.cos((entity.world.getTime() + tickDelta) * 2.7) / 156, 0);
        }
        var frameTexture = frame.model().texture();
        if (frameModel != null) frameModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(frameTexture)), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();

        var wheelBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getWheels().model().texture()));
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

        if (entity.getDriftTimer() > 0) {
            var texes = SkidEffectModel.SMOKE_TEXTURES;
            boolean bright = false;
            if (entity.getDriftTimer() > AutomobileEntity.DRIFT_TURBO_TIME) {
                texes = SkidEffectModel.FLAME_TEXTURES;
                bright = true;
            } else if (entity.getDriftTimer() > AutomobileEntity.DRIFT_TURBO_TIME - 20) {
                texes = SkidEffectModel.SPARK_TEXTURES;
            }
            int index = (int)((entity.world.getTime() / 2) % 3);
            var skidEffectBuffer = vertexConsumers.getBuffer(bright ? RenderLayer.getEyes(texes[index]) : RenderLayer.getEntityTranslucent(texes[index]));

            matrices.push();
            matrices.translate((sLong / 2) + (wheelRadius / 16), wheelRadius / 16, -((sWide / 2) + (wheels.model().width() / 16)));
            skidEffectModel.render(matrices, skidEffectBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 0.6f);
            matrices.pop();
            matrices.push();
            matrices.scale(1, 1, -1);
            matrices.translate((sLong / 2) + (wheelRadius / 16), wheelRadius / 16, -((sWide / 2) + (wheels.model().width() / 16)));
            skidEffectModel.render(matrices, skidEffectBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 0.6f);
            matrices.pop();
        }

        // -----------------------------------------------

        matrices.pop();
    }

    private void updateModels(AutomobileEntity entity) {
        frameModel = entity.getFrame().model().model().apply(this.ctx);
        wheelModel = entity.getWheels().model().model().apply(this.ctx);
        entity.updateModels = false;
    }
}
