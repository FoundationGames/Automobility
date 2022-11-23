package io.github.foundationgames.automobility.automobile.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class BaseModel extends Model {
    protected final ModelPart root;

    public BaseModel(Function<ResourceLocation, RenderType> layerFactory, EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
        super(layerFactory);
        this.root = ctx.bakeLayer(layer).getChild("main");
    }

    protected void prepare(PoseStack matrices) {
    }

    @Override
    public final void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.pushPose();
        this.prepare(matrices);
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        renderExtra(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.popPose();
    }

    public final void doOtherLayerRender(PoseStack matrices, MultiBufferSource consumers, int light, int overlay) {
        matrices.pushPose();
        this.prepare(matrices);
        this.renderOtherLayer(matrices, consumers, light, overlay);
        matrices.popPose();
    }

    public void renderExtra(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    }

    public void renderOtherLayer(PoseStack matrices, MultiBufferSource consumers, int light, int overlay) {
    }
}
