package io.github.foundationgames.automobility.automobile.render.wheel;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class TractorWheelModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/wheel/tractor"), "main");

    private final ModelPart main;
    private final ModelPart tire_8;
    private final ModelPart tire_7;
    private final ModelPart tire_6;
    private final ModelPart tire_5;
    private final ModelPart tire_4;
    private final ModelPart tire_3;
    private final ModelPart tire_2;

    public TractorWheelModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.tire_2 = this.main.getChild("tire_2");
        this.tire_3 = this.main.getChild("tire_3");
        this.tire_4 = this.main.getChild("tire_4");
        this.tire_5 = this.main.getChild("tire_5");
        this.tire_6 = this.main.getChild("tire_6");
        this.tire_7 = this.main.getChild("tire_7");
        this.tire_8 = this.main.getChild("tire_8");
        setRotationAngle(tire_8, -0.7854F, 0.0F, 0.0F);
        setRotationAngle(tire_7, -1.5708F, 0.0F, 0.0F);
        setRotationAngle(tire_6, -2.3562F, 0.0F, 0.0F);
        setRotationAngle(tire_5, 3.1416F, 0.0F, 0.0F);
        setRotationAngle(tire_4, 2.3562F, 0.0F, 0.0F);
        setRotationAngle(tire_3, 1.5708F, 0.0F, 0.0F);
        setRotationAngle(tire_2, 0.7854F, 0.0F, 0.0F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(0.0F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F).uv(0,5).cuboid(0.5F, -6.125F, -2.5F, 2.0F, 5.0F, 5.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("tire_8", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-0.5F,-3.625F,0.0F));
        partData.addChild("tire_7", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-0.5F,-3.625F,0.0F));
        partData.addChild("tire_6", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-0.5F,-3.625F,0.0F));
        partData.addChild("tire_5", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-0.5F,-3.625F,0.0F));
        partData.addChild("tire_4", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-0.5F,-3.625F,0.0F));
        partData.addChild("tire_3", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-0.5F,-3.625F,0.0F));
        partData.addChild("tire_2", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-0.5F,-3.625F,0.0F));
        return TexturedModelData.of(modelData,16,16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
