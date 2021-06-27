package io.github.foundationgames.automobility.entity.render;

import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class AutomobileEntityRenderer extends EntityRenderer<AutomobileEntity> {
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
        float xs = entity.getGroundSlopeX(tickDelta);
        float zs = entity.getGroundSlopeZ(tickDelta);
        float rd = entity.getWidth() / 2.5f;
        double h = Math.tan(Math.toRadians(Math.max(xs, zs))) * rd;
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(xs));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(zs));
        matrices.translate(0, -h, 0);
        AutomobileRenderer.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, tickDelta, entity.getFrame(), entity.getWheels(), entity.getEngine(), ctx, entity);
        matrices.pop();
    }
}
