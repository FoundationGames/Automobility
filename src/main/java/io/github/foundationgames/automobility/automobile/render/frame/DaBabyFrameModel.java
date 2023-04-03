package io.github.foundationgames.automobility.automobile.render.frame;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class DaBabyFrameModel extends Model {
    // Credit to https://github.com/Sk3leCreeper for model
    // private final BakedModel model = Myron.getModel(Automobility.id("models/misc/automobile/frame/dababy"));

    public DaBabyFrameModel(EntityRendererFactory.Context ctx) {
        super(id -> TexturedRenderLayers.getEntitySolid());
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        /*
        if (model == null) return;
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        matrices.translate(0.1, 0.04, 0);
        AUtils.renderMyronObj(model, vertices, matrices, light, overlay);
        matrices.pop();
         */
    }
}
