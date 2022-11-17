package io.github.foundationgames.automobility.automobile.render.wheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
// import dev.monarkhes.myron.api.Myron;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ConvertibleWheelModel extends Model {
    // Credit to https://github.com/Sk3leCreeper for model
    // private final BakedModel model = Myron.getModel(Automobility.id("models/misc/automobile/wheel/convertible"));

    public ConvertibleWheelModel(EntityRendererProvider.Context ctx) {
        super(id -> RenderType.solid());
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        /*
        if (model == null) return;
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        matrices.translate(0, -0.325, 0);
        AUtils.renderMyronObj(model, vertices, matrices, light, overlay);
        matrices.pop();
         */
    }
}
