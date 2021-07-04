package io.github.foundationgames.automobility.automobile.render.frame;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class PineappleFrameModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/frame/pineapple"), "main");

    private final ModelPart main;
    private final ModelPart stem;

    public PineappleFrameModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.stem = this.main.getChild("stem");
        setRotationAngle(stem, 0.0F, 0.0F, 0.0873F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-4.0F, -8.0F, -5.0F, 8.0F, 7.0F, 0.0F).uv(0,11).cuboid(-4.5F, -8.0F, -4.5F, 9.0F, 8.0F, 9.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("stem", ModelPartBuilder.create().uv(0,7).cuboid(-10.0F, -10.5F, -3.0F, 1.0F, 3.0F, 1.0F), ModelTransform.pivot(8.5F,0.5F,2.5F));
        return TexturedModelData.of(modelData,64,32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.translate(0, -1.5, 0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        matrices.scale(2, 2, 2);
        this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
