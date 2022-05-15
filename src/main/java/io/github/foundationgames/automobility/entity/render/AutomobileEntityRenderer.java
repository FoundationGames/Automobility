package io.github.foundationgames.automobility.entity.render;

import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
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
        float angX = entity.getDisplacement().getAngularX(tickDelta);
        float angZ = entity.getDisplacement().getAngularZ(tickDelta);
        float offsetY = entity.getDisplacement().getVertical(tickDelta);

        matrices.translate(0, offsetY, 0);
        matrices.multiply(Quaternion.fromEulerXyz((float) Math.toRadians(angX), 0, (float) Math.toRadians(angZ)));
        AutomobileRenderer.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, tickDelta, ctx, entity);
        matrices.pop();
    }
}
