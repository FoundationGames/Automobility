package io.github.foundationgames.automobility.automobile.render.engine;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

public class IronEngineModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/engine/iron"), "main");

    private final ModelPart right_exhaust;
    private final ModelPart right_top;
    private final ModelPart left_exhaust;
    private final ModelPart left_top;
    private final ModelPart main;

    public IronEngineModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        var root = ctx.getPart(MODEL_LAYER);
        this.right_exhaust = root.getChild("right_exhaust");
        this.right_top = this.right_exhaust.getChild("right_top");
        this.left_exhaust = root.getChild("left_exhaust");
        this.left_top = this.left_exhaust.getChild("left_top");
        this.main = root.getChild("main");
        setRotationAngle(right_exhaust, 0.4363F, 0.0F, 0.0F);
        setRotationAngle(right_top, 0.3491F, 0.0F, 0.0F);
        setRotationAngle(left_exhaust, 0.4363F, 0.0F, 0.0F);
        setRotationAngle(left_top, 0.3491F, 0.0F, 0.0F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData rightExhaust = root.addChild("right_exhaust", ModelPartBuilder.create().uv(0,0).cuboid(-0.5F, -3.0F, 0.0F, 2.0F, 3.0F, 2.0F), ModelTransform.pivot(3.0F,24.0F,-5.0F));
        rightExhaust.addChild("right_top", ModelPartBuilder.create().uv(25,0).cuboid(0.0F, -4.0F, -2.5F, 3.0F, 4.0F, 3.0F), ModelTransform.pivot(-1.0F,-3.0F,2.0F));
        ModelPartData leftExhaust = root.addChild("left_exhaust", ModelPartBuilder.create().uv(0,0).cuboid(-0.5F, -3.0F, 0.0F, 2.0F, 3.0F, 2.0F), ModelTransform.pivot(-4.0F,24.0F,-5.0F));
        leftExhaust.addChild("left_top", ModelPartBuilder.create().uv(25,0).cuboid(0.0F, -4.0F, -2.5F, 3.0F, 4.0F, 3.0F), ModelTransform.pivot(-1.0F,-3.0F,2.0F));
        root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 9.0F).uv(0,14).cuboid(-3.0F, -7.0F, -4.0F, 6.0F, 2.0F, 7.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        return TexturedModelData.of(modelData,64,32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        left_exhaust.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        right_exhaust.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
