package io.github.foundationgames.automobility.automobile.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class EmptyModel extends Model {
    public EmptyModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntitySolid);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
    }
}
