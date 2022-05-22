package io.github.foundationgames.automobility.block.entity.render;

import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class AutomobileAssemblerBlockEntityRenderer implements BlockEntityRenderer<AutomobileAssemblerBlockEntity> {
    private EntityRendererFactory.Context context = null;

    public AutomobileAssemblerBlockEntityRenderer() {
        EntityRenderHelper.registerContextListener(this::setContext);
    }

    public void setContext(EntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(AutomobileAssemblerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (this.context != null) {
            matrices.push();

            matrices.translate(0.5, 0.75 - (entity.getWheels().model().radius() / 16), 0.5);
            AutomobileRenderer.render(matrices, vertexConsumers, light, overlay, tickDelta, this.context, entity);

            matrices.pop();
        }
    }
}
