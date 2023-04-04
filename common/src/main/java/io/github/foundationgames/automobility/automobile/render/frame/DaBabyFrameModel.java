package io.github.foundationgames.automobility.automobile.render.frame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DaBabyFrameModel extends Model {
    // Credit to https://github.com/Sk3leCreeper for model
    // private final BakedModel model = Myron.getModel(Automobility.id("models/misc/automobile/frame/dababy"));

    public DaBabyFrameModel(EntityRendererProvider.Context ctx) {
        super(id -> Sheets.solidBlockSheet());
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        /*
        if (model == null) return;
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        matrices.translate(0.1, 0.04, 0);
        AUtils.renderMyronObj(model, vertices, matrices, light, overlay);
        matrices.pop();
         */
    }
}
