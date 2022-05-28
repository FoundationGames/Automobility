package io.github.foundationgames.automobility.block.entity.render;

import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Vec3f;

public class AutomobileAssemblerBlockEntityRenderer implements BlockEntityRenderer<AutomobileAssemblerBlockEntity> {
    private final TextRenderer textRenderer;
    private EntityRendererFactory.Context context = null;

    public AutomobileAssemblerBlockEntityRenderer(BlockEntityRendererFactory.Context blockEntityCtx) {
        EntityRenderHelper.registerContextListener(this::setContext);

        this.textRenderer = blockEntityCtx.getTextRenderer();
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

            matrices.push();
            matrices.translate(0.5, 0, 0.5);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-entity.getCachedState().get(Properties.HORIZONTAL_FACING).asRotation()));
            matrices.translate(0, 0.372, 0.501);
            matrices.scale(0.008f, -0.008f, -0.008f);

            for (var text : entity.label) {
                matrices.push();
                matrices.translate(-0.5 * textRenderer.getWidth(text), 0, 0);
                textRenderer.drawWithShadow(matrices, text, 0, 0, 0xFFFFFF);
                matrices.pop();
                matrices.translate(0, 12, 0);
            }

            matrices.pop();
        }
    }
}
