package io.github.foundationgames.automobility.automobile.render.engine;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class CreativeEngineModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/engine/creative"), "main");

    private final ModelPart main;

    public CreativeEngineModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F).uv(0,15).cuboid(-2.0F, -9.0F, -3.0F, 4.0F, 4.0F, 10.0F).uv(0,0).cuboid(-2.0F, -9.0F, 6.0F, 4.0F, 4.0F, 0.0F).uv(20,22).cuboid(-3.0F, -7.0F, -4.0F, 6.0F, 2.0F, 8.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        return TexturedModelData.of(modelData,64,32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
