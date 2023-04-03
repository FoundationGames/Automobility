package io.github.foundationgames.automobility.automobile.render.wheel;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class ConvertibleWheelModel extends Model {
    // Credit to https://github.com/Sk3leCreeper for model
    // private final BakedModel model = Myron.getModel(Automobility.id("models/misc/automobile/wheel/convertible"));

    public ConvertibleWheelModel(EntityRendererFactory.Context ctx) {
        super(id -> RenderLayer.getSolid());
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        /*
        if (model == null) return;
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        matrices.translate(0, -0.325, 0);
        AUtils.renderMyronObj(model, vertices, matrices, light, overlay);
        matrices.pop();
         */
    }
}
