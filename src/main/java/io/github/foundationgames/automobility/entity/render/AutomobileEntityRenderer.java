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
        float hw = entity.getWidth() / 2;
        float hh = entity.getHeight() / 2;

        // yea
        float angle = Math.max(Math.abs(xs), Math.abs(zs));
        double heightOffset = (Math.sin(Math.toRadians(angle)) * (Math.tan(Math.toRadians(180 - angle)) * hh + hw)) - Math.sin(Math.toRadians(angle) * hw);

        matrices.translate(0, hh, 0);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(xs));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(zs));
        matrices.translate(0, -hh, 0);
        matrices.translate(0, heightOffset, 0);
        AutomobileRenderer.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, tickDelta, entity.getFrame(), entity.getWheels(), entity.getEngine(), ctx, entity);
        matrices.pop();
    }
}
