package io.github.foundationgames.automobility.automobile.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class BaseModel extends Model {
    protected final ModelPart root;

    public BaseModel(Function<Identifier, RenderLayer> layerFactory, EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(layerFactory);
        this.root = ctx.getPart(layer).getChild("main");
    }

    protected void prepare(MatrixStack matrices) {
    }

    @Override
    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        this.prepare(matrices);
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        renderExtra(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    public final void doOtherLayerRender(MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay) {
        matrices.push();
        this.prepare(matrices);
        this.renderOtherLayer(matrices, consumers, light, overlay);
        matrices.pop();
    }

    public void renderExtra(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    }

    public void renderOtherLayer(MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay) {
    }
}
