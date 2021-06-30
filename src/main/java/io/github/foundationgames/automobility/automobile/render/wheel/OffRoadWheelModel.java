package io.github.foundationgames.automobility.automobile.render.wheel;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

public class OffRoadWheelModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/wheel/off_road"), "main");

    private final ModelPart main;
    private final ModelPart spoke_2;
    private final ModelPart spoke_8;
    private final ModelPart spoke_7;
    private final ModelPart spoke_6;
    private final ModelPart spoke_5;
    private final ModelPart spoke_4;
    private final ModelPart spoke_3;

    public OffRoadWheelModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.spoke_3 = this.main.getChild("spoke_3");
        this.spoke_4 = this.main.getChild("spoke_4");
        this.spoke_5 = this.main.getChild("spoke_5");
        this.spoke_6 = this.main.getChild("spoke_6");
        this.spoke_7 = this.main.getChild("spoke_7");
        this.spoke_8 = this.main.getChild("spoke_8");
        this.spoke_2 = this.main.getChild("spoke_2");
        setRotationAngle(spoke_2, 0.0F, 0.0F, 0.7854F);
        setRotationAngle(spoke_8, 0.0F, 0.0F, -0.7854F);
        setRotationAngle(spoke_7, 0.0F, 0.0F, -1.5708F);
        setRotationAngle(spoke_6, 0.0F, 0.0F, -2.3562F);
        setRotationAngle(spoke_5, 0.0F, 0.0F, -3.1416F);
        setRotationAngle(spoke_4, 0.0F, 0.0F, 2.3562F);
        setRotationAngle(spoke_3, 0.0F, 0.0F, 1.5708F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-3.5F, -16.8F, -5.0F, 7.0F, 3.0F, 5.0F).uv(0,8).cuboid(-5.5F, -13.9F, -3.0F, 11.0F, 11.0F, 2.0F).uv(0,21).cuboid(-1.5F, -9.9F, -4.0F, 3.0F, 3.0F, 1.0F).uv(8,21).cuboid(-1.0F, -14.9F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,21).cuboid(-1.5F, -9.9F, -1.0F, 3.0F, 3.0F, 1.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("spoke_2", ModelPartBuilder.create().uv(8,21).cuboid(-1.0F, -6.5F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,0).cuboid(-3.5F, -8.4F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-8.4F,0.0F));
        partData.addChild("spoke_8", ModelPartBuilder.create().uv(8,21).cuboid(-1.0F, -6.5F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,0).cuboid(-3.5F, -8.4F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-8.4F,0.0F));
        partData.addChild("spoke_7", ModelPartBuilder.create().uv(8,21).cuboid(-1.0F, -6.5F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,0).cuboid(-3.5F, -8.4F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-8.4F,0.0F));
        partData.addChild("spoke_6", ModelPartBuilder.create().uv(8,21).cuboid(-1.0F, -6.5F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,0).cuboid(-3.5F, -8.4F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-8.4F,0.0F));
        partData.addChild("spoke_5", ModelPartBuilder.create().uv(8,21).cuboid(-1.0F, -6.5F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,0).cuboid(-3.5F, -8.4F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-8.4F,0.0F));
        partData.addChild("spoke_4", ModelPartBuilder.create().uv(8,21).cuboid(-1.0F, -6.5F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,0).cuboid(-3.5F, -8.4F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-8.4F,0.0F));
        partData.addChild("spoke_3", ModelPartBuilder.create().uv(8,21).cuboid(-1.0F, -6.5F, -3.75F, 2.0F, 6.0F, 1.0F).uv(0,0).cuboid(-3.5F, -8.4F, -5.0F, 7.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-8.4F,0.0F));
        return TexturedModelData.of(modelData,32,32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
