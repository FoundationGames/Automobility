package io.github.foundationgames.automobility.automobile.render.wheel;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class SteelWheelModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/wheel/steel"), "main");

    private final ModelPart main;
    private final ModelPart wheel_8;
    private final ModelPart wheel_7;
    private final ModelPart wheel_6;
    private final ModelPart wheel_5;
    private final ModelPart wheel_4;
    private final ModelPart wheel_3;
    private final ModelPart wheel_2;

    public SteelWheelModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.wheel_2 = this.main.getChild("wheel_2");
        this.wheel_3 = this.main.getChild("wheel_3");
        this.wheel_4 = this.main.getChild("wheel_4");
        this.wheel_5 = this.main.getChild("wheel_5");
        this.wheel_6 = this.main.getChild("wheel_6");
        this.wheel_7 = this.main.getChild("wheel_7");
        this.wheel_8 = this.main.getChild("wheel_8");
        setRotationAngle(wheel_8, -0.7854F, 0.0F, 0.0F);
        setRotationAngle(wheel_7, -1.5708F, 0.0F, 0.0F);
        setRotationAngle(wheel_6, -2.3562F, 0.0F, 0.0F);
        setRotationAngle(wheel_5, 3.1416F, 0.0F, 0.0F);
        setRotationAngle(wheel_4, 2.3562F, 0.0F, 0.0F);
        setRotationAngle(wheel_3, 1.5708F, 0.0F, 0.0F);
        setRotationAngle(wheel_2, 0.7854F, 0.0F, 0.0F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(0.5F, -2.0F, -1.5F, 2.0F, 2.0F, 3.0F).uv(10,0).cuboid(0.0F, -2.2F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,5).cuboid(0.25F, -5.125F, -1.5F, 2.0F, 3.0F, 3.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("wheel_8", ModelPartBuilder.create().uv(10,0).cuboid(0.0F, 1.425F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 2.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.625F,0.0F));
        partData.addChild("wheel_7", ModelPartBuilder.create().uv(10,0).cuboid(0.0F, 1.425F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 2.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.625F,0.0F));
        partData.addChild("wheel_6", ModelPartBuilder.create().uv(10,0).cuboid(0.0F, 1.425F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 2.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.625F,0.0F));
        partData.addChild("wheel_5", ModelPartBuilder.create().uv(10,0).cuboid(0.0F, 1.425F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 2.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.625F,0.0F));
        partData.addChild("wheel_4", ModelPartBuilder.create().uv(10,0).cuboid(0.0F, 1.425F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 2.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.625F,0.0F));
        partData.addChild("wheel_3", ModelPartBuilder.create().uv(10,0).cuboid(0.0F, 1.425F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 2.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.625F,0.0F));
        partData.addChild("wheel_2", ModelPartBuilder.create().uv(10,0).cuboid(0.0F, 1.425F, -1.0F, 3.0F, 1.0F, 2.0F).uv(0,0).cuboid(0.5F, 1.625F, -1.5F, 2.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.625F,0.0F));
        return TexturedModelData.of(modelData,32,32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
