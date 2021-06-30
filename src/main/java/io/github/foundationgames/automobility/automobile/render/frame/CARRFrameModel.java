package io.github.foundationgames.automobility.automobile.render.frame;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

public class CARRFrameModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/frame/c_arr"), "main");

    private final ModelPart main;
    private final ModelPart engine_chain;
    private final ModelPart rod_diagonal;
    private final ModelPart engine_support;
    private final ModelPart front_axle;
    private final ModelPart rear_axle;
    private final ModelPart chassis_right;

    public CARRFrameModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.chassis_right = this.main.getChild("chassis_right");
        this.rear_axle = this.main.getChild("rear_axle");
        this.front_axle = this.main.getChild("front_axle");
        this.engine_support = this.main.getChild("engine_support");
        this.rod_diagonal = this.main.getChild("rod_diagonal");
        this.engine_chain = this.main.getChild("engine_chain");
        setRotationAngle(engine_chain, 0.0F, 0.0F, -0.2749F);
        setRotationAngle(rod_diagonal, 0.0F, -0.7418F, 0.0F);
        setRotationAngle(engine_support, 0.0F, 0.0F, -1.309F);
        setRotationAngle(front_axle, 0.0F, 0.0F, -0.3927F);
        setRotationAngle(rear_axle, 0.0F, 0.0F, 0.3927F);
        setRotationAngle(chassis_right, 0.0F, 3.1416F, 0.0F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-12.0F, -6.0F, -8.0F, 24.0F, 3.0F, 16.0F).uv(0,19).cuboid(-12.0F, -13.0F, -8.0F, 24.0F, 7.0F, 2.0F).uv(0,28).cuboid(10.0F, -13.0F, -6.0F, 2.0F, 7.0F, 12.0F).uv(0,28).cuboid(-12.0F, -13.0F, -6.0F, 2.0F, 7.0F, 12.0F).uv(32,28).cuboid(17.0F, -14.0F, 0.0F, 2.0F, 2.0F, 8.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("engine_chain", ModelPartBuilder.create().uv(52,23).cuboid(-1.425F, -15.25F, 0.0F, 3.0F, 17.0F, 1.0F), ModelTransform.pivot(21.725F,0.225F,6.0F));
        partData.addChild("rod_diagonal", ModelPartBuilder.create().uv(52,19).cuboid(-9.975F, -1.0F, 0.0F, 10.0F, 2.0F, 2.0F), ModelTransform.pivot(18.3F,-9.0F,-1.0F));
        partData.addChild("engine_support", ModelPartBuilder.create().uv(52,19).cuboid(-1.4F, -1.925F, -1.0F, 10.0F, 2.0F, 2.0F), ModelTransform.pivot(17.0F,-3.0F,0.0F));
        partData.addChild("front_axle", ModelPartBuilder.create().uv(12,31).cuboid(-12.0F, 0.0F, -8.0F, 2.0F, 2.0F, 16.0F).uv(52,19).cuboid(-10.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F), ModelTransform.pivot(-12.0F,-5.0F,0.0F));
        partData.addChild("rear_axle", ModelPartBuilder.create().uv(12,31).cuboid(10.0F, 0.0F, -8.0F, 2.0F, 2.0F, 16.0F).uv(52,19).cuboid(0.0F, 0.0F, -1.0F, 10.0F, 2.0F, 2.0F), ModelTransform.pivot(12.0F,-5.0F,0.0F));
        partData.addChild("chassis_right", ModelPartBuilder.create().uv(0,19).cuboid(-12.0F, -13.0F, -1.0F, 24.0F, 7.0F, 2.0F), ModelTransform.pivot(0.0F,0.0F,7.0F));
        return TexturedModelData.of(modelData,128,64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}