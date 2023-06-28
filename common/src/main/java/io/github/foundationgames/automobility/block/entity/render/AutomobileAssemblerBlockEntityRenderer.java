package io.github.foundationgames.automobility.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AutomobileAssemblerBlockEntityRenderer implements BlockEntityRenderer<AutomobileAssemblerBlockEntity> {
    private final Font textRenderer;

    public AutomobileAssemblerBlockEntityRenderer(BlockEntityRendererProvider.Context blockEntityCtx) {
        this.textRenderer = blockEntityCtx.getFont();
    }

    @Override
    public void render(AutomobileAssemblerBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        matrices.translate(0.5, 0.75 - (entity.getWheels().model().radius() / 16), 0.5);
        AutomobileRenderer.render(matrices, vertexConsumers, light, overlay, tickDelta, entity);
        matrices.popPose();

        matrices.pushPose();
        matrices.translate(0.5, 0, 0.5);
        matrices.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        matrices.translate(0, 0.372, 0.501);
        matrices.scale(0.008f, -0.008f, 0.008f);

        for (var text : entity.label) {
            matrices.pushPose();
            matrices.translate(-0.5 * textRenderer.width(text), 0, 0);
            textRenderer.drawInBatch(text, 0f, 0f, 0xFFFFFF, true, matrices.last().pose(), vertexConsumers, Font.DisplayMode.POLYGON_OFFSET, 0, light);
            matrices.popPose();
            matrices.translate(0, 12, 0);
        }

        matrices.popPose();
    }
}
