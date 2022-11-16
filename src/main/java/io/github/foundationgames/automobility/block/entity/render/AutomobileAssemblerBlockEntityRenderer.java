package io.github.foundationgames.automobility.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AutomobileAssemblerBlockEntityRenderer implements BlockEntityRenderer<AutomobileAssemblerBlockEntity> {
    private final Font textRenderer;
    private EntityRendererProvider.Context context = null;

    public AutomobileAssemblerBlockEntityRenderer(BlockEntityRendererProvider.Context blockEntityCtx) {
        EntityRenderHelper.registerContextListener(this::setContext);

        this.textRenderer = blockEntityCtx.getFont();
    }

    public void setContext(EntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(AutomobileAssemblerBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (this.context != null) {
            matrices.pushPose();
            matrices.translate(0.5, 0.75 - (entity.getWheels().model().radius() / 16), 0.5);
            AutomobileRenderer.render(matrices, vertexConsumers, light, overlay, tickDelta, this.context, entity);
            matrices.popPose();

            matrices.pushPose();
            matrices.translate(0.5, 0, 0.5);
            matrices.mulPose(Vector3f.YP.rotationDegrees(-entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
            matrices.translate(0, 0.372, 0.501);
            matrices.scale(0.008f, -0.008f, -0.008f);

            for (var text : entity.label) {
                matrices.pushPose();
                matrices.translate(-0.5 * textRenderer.width(text), 0, 0);
                textRenderer.drawShadow(matrices, text, 0, 0, 0xFFFFFF);
                matrices.popPose();
                matrices.translate(0, 12, 0);
            }

            matrices.popPose();
        }
    }
}
