package io.github.foundationgames.automobility.automobile.render.frame;

import dev.monarkhēs.myron.api.Myron;
import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

public class DaBabyFrameModel extends Model {
    private final BakedModel frameModel = Myron.getModel(Automobility.id("models/misc/dababy"));

    private static final Random RANDOM = new Random();

    public DaBabyFrameModel(EntityRendererFactory.Context ctx) {
        super(id -> RenderLayer.getSolid());
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (frameModel == null) return;
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.translate(0.1, -1.46, 0);
        for (BakedQuad quad : frameModel.getQuads(null, null, RANDOM)) {
            vertices.quad(matrices.peek(), quad, 1, 1, 1, light, overlay);
        }
        matrices.pop();
    }
}
