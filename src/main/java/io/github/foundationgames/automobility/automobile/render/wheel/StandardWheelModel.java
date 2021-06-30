package io.github.foundationgames.automobility.automobile.render.wheel;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

public class StandardWheelModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/wheel/standard"), "main");

    private final ModelPart main;
    private final ModelPart tire_top_right;
    private final ModelPart tire_top_left;
    private final ModelPart tire_bottom_left;
    private final ModelPart tire_bottom_right;
    private final ModelPart tire_left;

    public StandardWheelModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.tire_left = this.main.getChild("tire_left");
        this.tire_bottom_right = this.main.getChild("tire_bottom_right");
        this.tire_bottom_left = this.main.getChild("tire_bottom_left");
        this.tire_top_left = this.main.getChild("tire_top_left");
        this.tire_top_right = this.main.getChild("tire_top_right");
        setRotationAngle(tire_top_right, 0.0F, 0.0F, -0.7854F);
        setRotationAngle(tire_top_left, 0.0F, 0.0F, -2.3562F);
        setRotationAngle(tire_bottom_left, 0.0F, 0.0F, 2.3562F);
        setRotationAngle(tire_bottom_right, 0.0F, 0.0F, 0.7854F);
        setRotationAngle(tire_left, 0.0F, 0.0F, 1.5708F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-1.5F, -6.0F, -8.0F, 3.0F, 1.0F, 3.0F).uv(8,5).cuboid(-2.0F, -5.0F, -7.0F, 4.0F, 4.0F, 1.0F).uv(0,0).cuboid(-1.5F, -1.0F, -8.0F, 3.0F, 1.0F, 3.0F), ModelTransform.pivot(0.0F,24.0F,5.0F));
        partData.addChild("tire_top_right", ModelPartBuilder.create().uv(0,4).cuboid(2.182F, -1.0143F, -1.0F, 1.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.0F,-7.0F));
        partData.addChild("tire_top_left", ModelPartBuilder.create().uv(0,4).cuboid(2.182F, -1.0143F, -1.0F, 1.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.0F,-7.0F));
        partData.addChild("tire_bottom_left", ModelPartBuilder.create().uv(0,4).cuboid(2.182F, -1.0143F, -1.0F, 1.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.0F,-7.0F));
        partData.addChild("tire_bottom_right", ModelPartBuilder.create().uv(0,4).cuboid(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 3.0F), ModelTransform.pivot(1.9066F,-1.1137F,-7.0F));
        partData.addChild("tire_left", ModelPartBuilder.create().uv(0,0).cuboid(1.0F, 2.0F, -1.0F, 3.0F, 1.0F, 3.0F).uv(0,0).cuboid(1.0F, -3.0F, -1.0F, 3.0F, 1.0F, 3.0F), ModelTransform.pivot(0.0F,-5.5F,-7.0F));
        return TexturedModelData.of(modelData,32,32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
