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
        float chassisRaise = wheels.model().radiusPx() / 16;

        matrices.translate(0, -1.5 - chassisRaise, 0);
        if (entity.updateModels) {
            updateModels(entity);
        }
        var frameTexture = entity.getFrame().model().texture();
        frameModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(frameTexture)), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        var wheelBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getWheels().model().texture()));
        float longs = 26f / 16;
        float wide = 10f / 16;

        // WHEELS ----------------------------------------

        // Front left
        matrices.push();
        matrices.translate(-(longs / 2), wheels.model().radiusPx() / 16, -(wide / 2));
        wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();

        // Rear left
        matrices.push();
        matrices.translate(longs / 2, wheels.model().radiusPx() / 16, -(wide / 2));
        wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();

        // Front right
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        matrices.translate(-(longs / 2), wheels.model().radiusPx() / 16, -(wide / 2));
        wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();

        // Rear right
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        matrices.translate(longs / 2, wheels.model().radiusPx() / 16, -(wide / 2));
        wheelModel.render(matrices, wheelBuffer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        matrices.pop();

        // -----------------------------------------------

        matrices.pop();
    }

    private void updateModels(AutomobileEntity entity) {
        frameModel = entity.getFrame().model().model().apply(this.ctx);
        wheelModel = entity.getWheels().model().model().apply(this.ctx);
        entity.updateModels = false;
    }
}
