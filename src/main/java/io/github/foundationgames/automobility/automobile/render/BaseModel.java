package io.github.foundationgames.automobility.automobile.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
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

    protected void transform(MatrixStack matrices) {
    }

    @Override
    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        this.transform(matrices);
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        renderOther(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    public void renderOther(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    }
}
