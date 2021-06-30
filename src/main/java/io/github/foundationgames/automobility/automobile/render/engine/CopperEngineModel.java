package io.github.foundationgames.automobility.automobile.render.engine;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class CopperEngineModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/engine/copper"), "main");

    private final ModelPart main;
    private final ModelPart exhaust;
    private final ModelPart pipe_side;

    public CopperEngineModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        var root = ctx.getPart(MODEL_LAYER);
        this.main = root.getChild("main");
        this.pipe_side = this.main.getChild("pipe_side");
        this.exhaust = this.main.getChild("exhaust");
        setRotationAngle(exhaust, 0.3054F, 0.0F, 0.0F);
        setRotationAngle(pipe_side, 1.1345F, 0.0F, 0.0F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 9.0F).uv(0,13).cuboid(-3.0F, -5.0F, -3.0F, 6.0F, 1.0F, 7.0F).uv(0,21).cuboid(-3.5F, -6.0F, -3.5F, 7.0F, 1.0F, 8.0F).uv(25,0).cuboid(-3.0F, -2.0F, 4.0F, 8.0F, 2.0F, 2.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("exhaust", ModelPartBuilder.create().uv(19,13).cuboid(-3.5F, -2.5F, 0.0F, 3.0F, 3.0F, 4.0F), ModelTransform.pivot(0.0F,0.0F,6.0F));
        partData.addChild("pipe_side", ModelPartBuilder.create().uv(0,21).cuboid(3.0F, -5.0F, 0.0F, 2.0F, 5.0F, 2.0F), ModelTransform.pivot(0.0F,0.0F,4.0F));
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
