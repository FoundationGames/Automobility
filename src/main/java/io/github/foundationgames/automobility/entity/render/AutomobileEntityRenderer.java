package io.github.foundationgames.automobility.entity.render;

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

    private final EntityRendererFactory.Context ctx;

    public AutomobileEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.ctx = ctx;
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
        float chassisRaise = wheels.model().radiusPx() / 16;

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

        if (wheelModel != null) {
            // Front left
            matrices.push();
            matrices.translate(-(sLong / 2), wheels.model().radiusPx() / 16, -(sWide / 2));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getSteering(tickDelta) * 27));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();

            // Rear left
            matrices.push();
            matrices.translate(sLong / 2, wheels.model().radiusPx() / 16, -(sWide / 2));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();

            // Rear right
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            matrices.translate(-(sLong / 2), wheels.model().radiusPx() / 16, -(sWide / 2));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();

            // Front right
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            matrices.translate(sLong / 2, wheels.model().radiusPx() / 16, -(sWide / 2));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getSteering(tickDelta) * 27));
            matrices.translate(0, raise, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(wheelAngle));
            matrices.translate(0, -raise, 0);
            wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
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
