package io.github.foundationgames.automobility.automobile.render.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.front.FrontAttachment;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class FrontAttachmentRenderModel extends BaseModel {
    protected final @Nullable ModelPart ground;
    private float groundHeight = 0;

    public FrontAttachmentRenderModel(Function<Identifier, RenderLayer> layerFactory, EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(layerFactory, ctx, layer);
        ModelPart ground;
        try {
            ground = ctx.getPart(layer).getChild("ground");
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
    public void renderExtra(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.ground != null) {
            matrices.push();
            matrices.translate(0, groundHeight, 0);
            this.ground.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            matrices.pop();
        }
    }
}
