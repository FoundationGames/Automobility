package io.github.foundationgames.automobility.automobile.render.attachment.front;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.foundationgames.automobility.automobile.attachment.front.FrontAttachment;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class FrontAttachmentRenderModel extends BaseModel {
    protected final @Nullable ModelPart ground;
    private float groundHeight = 0;

    public FrontAttachmentRenderModel(Function<ResourceLocation, RenderType> layerFactory, EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
        super(layerFactory, ctx, layer);
        ModelPart ground;
        try {
            ground = ctx.bakeLayer(layer).getChild("ground");
        } catch (NoSuchElementException ignored) {
            ground = null;
        }
        this.ground = ground;
    }

    public void setRenderState(@Nullable FrontAttachment attachment, float groundHeight, float tickDelta) {
        this.groundHeight = groundHeight;
    }

    public void resetModel() {
        this.setRenderState(null, 0, 0);
    }

    @Override
    public void renderExtra(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.ground != null) {
            matrices.pushPose();
            matrices.translate(0, groundHeight, 0);
            this.ground.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            matrices.popPose();
        }
    }
}
