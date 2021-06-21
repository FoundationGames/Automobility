package io.github.foundationgames.automobility.automobile.render.frame;

import io.github.foundationgames.automobility.Automobility;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Function;

public class StandardFrameModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile_frame/standard"), "main");

    private final ModelPart main;
    private final ModelPart rear_mudguard_back_left;
    private final ModelPart rear_mudguard_center_right;
    private final ModelPart rear_mudguard_back_right;
    private final ModelPart front_mudguard_front_right;
    private final ModelPart front_mudguard_front_left;
    private final ModelPart rear_mudguard_front_left;
    private final ModelPart rear_mudguard_center_left;
    private final ModelPart rear_mudguard_front_right;
    private final ModelPart seat_back;
    private final ModelPart left_wall;
    private final ModelPart front_mudguard_center_left;
    private final ModelPart front_mudguard_back_left;
    private final ModelPart front_mudguard_back_right;
    private final ModelPart front_mudguard_center_right;
    private final ModelPart hood;

    public StandardFrameModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.hood = this.main.getChild("hood");
        this.front_mudguard_center_right = this.main.getChild("front_mudguard_center_right");
        this.front_mudguard_back_right = this.main.getChild("front_mudguard_back_right");
        this.front_mudguard_back_left = this.main.getChild("front_mudguard_back_left");
        this.front_mudguard_center_left = this.main.getChild("front_mudguard_center_left");
        this.left_wall = this.main.getChild("left_wall");
        this.seat_back = this.main.getChild("seat_back");
        this.rear_mudguard_front_right = this.main.getChild("rear_mudguard_front_right");
        this.rear_mudguard_center_left = this.main.getChild("rear_mudguard_center_left");
        this.rear_mudguard_front_left = this.main.getChild("rear_mudguard_front_left");
        this.front_mudguard_front_left = this.main.getChild("front_mudguard_front_left");
        this.front_mudguard_front_right = this.main.getChild("front_mudguard_front_right");
        this.rear_mudguard_back_right = this.main.getChild("rear_mudguard_back_right");
        this.rear_mudguard_center_right = this.main.getChild("rear_mudguard_center_right");
        this.rear_mudguard_back_left = this.main.getChild("rear_mudguard_back_left");
        setRotationAngle(rear_mudguard_back_left, 3.1416F, 0.0F, 2.3562F);
        setRotationAngle(rear_mudguard_center_right, 0.0F, 0.0F, 1.5708F);
        setRotationAngle(rear_mudguard_back_right, 0.0F, 0.0F, 2.3562F);
        setRotationAngle(front_mudguard_front_right, 0.0F, 0.0F, 0.7854F);
        setRotationAngle(front_mudguard_front_left, 3.1416F, 0.0F, 0.7854F);
        setRotationAngle(rear_mudguard_front_left, 3.1416F, 0.0F, 0.7854F);
        setRotationAngle(rear_mudguard_center_left, 3.1416F, 0.0F, 1.5708F);
        setRotationAngle(rear_mudguard_front_right, 0.0F, 0.0F, 0.7854F);
        setRotationAngle(seat_back, 0.0F, 0.0F, 1.789F);
        setRotationAngle(left_wall, 0.0F, 3.1416F, 0.0F);
        setRotationAngle(front_mudguard_center_left, 3.1416F, 0.0F, 1.5708F);
        setRotationAngle(front_mudguard_back_left, 3.1416F, 0.0F, 2.3562F);
        setRotationAngle(front_mudguard_back_right, 0.0F, 0.0F, 2.3562F);
        setRotationAngle(front_mudguard_center_right, 0.0F, 0.0F, 1.5708F);
        setRotationAngle(hood, 0.0F, 0.0F, -0.3927F);
    }

    public void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,45).cuboid(-8.0F, -3.0F, -8.0F, 16.0F, 3.0F, 16.0F).uv(0,0).cuboid(-18.0F, -3.0F, -5.0F, 10.0F, 3.0F, 10.0F).uv(0,0).cuboid(8.0F, -3.0F, -5.0F, 10.0F, 3.0F, 10.0F).uv(1,27).cuboid(-22.0F, -2.0F, -8.0F, 4.0F, 2.0F, 16.0F).uv(3,0).cuboid(-15.0F, -5.55F, -5.0F, 7.0F, 3.0F, 10.0F).uv(26,36).cuboid(-8.0F, -7.0F, -8.0F, 16.0F, 4.0F, 3.0F).uv(30,26).cuboid(-2.0F, -5.0F, -4.0F, 9.0F, 2.0F, 8.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("rear_mudguard_back_left", ModelPartBuilder.create().uv(0,53).cuboid(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(16.9392F,-3.4287F,6.5F));
        partData.addChild("rear_mudguard_center_right", ModelPartBuilder.create().uv(0,53).cuboid(-0.0707F, -4.9649F, -8.0F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(10.575F,-5.475F,0.0F));
        partData.addChild("rear_mudguard_back_right", ModelPartBuilder.create().uv(0,53).cuboid(0.0F, -5.0F, -8.0F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(15.525F,-5.55F,0.0F));
        partData.addChild("front_mudguard_front_right", ModelPartBuilder.create().uv(0,53).cuboid(0.0F, -5.0F, -8.0F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(-19.0F,-2.0F,0.0F));
        partData.addChild("front_mudguard_front_left", ModelPartBuilder.create().uv(0,53).cuboid(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(-16.8787F,-3.4142F,6.5F));
        partData.addChild("rear_mudguard_front_left", ModelPartBuilder.create().uv(0,53).cuboid(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(9.1213F,-3.4142F,6.5F));
        partData.addChild("rear_mudguard_center_left", ModelPartBuilder.create().uv(0,53).cuboid(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(13.0399F,-5.0457F,6.5F));
        partData.addChild("rear_mudguard_front_right", ModelPartBuilder.create().uv(0,53).cuboid(0.0F, -5.0F, -8.0F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(7.0F,-2.0F,0.0F));
        partData.addChild("seat_back", ModelPartBuilder.create().uv(30,26).cuboid(-10.0F, 0.25F, -4.0F, 9.0F, 2.0F, 8.0F), ModelTransform.pivot(7.0F,-3.0F,0.0F));
        partData.addChild("left_wall", ModelPartBuilder.create().uv(26,36).cuboid(-8.0F, -2.0F, -1.5F, 16.0F, 4.0F, 3.0F), ModelTransform.pivot(0.0F,-5.0F,6.5F));
        partData.addChild("front_mudguard_center_left", ModelPartBuilder.create().uv(0,53).cuboid(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(-12.9601F,-5.0457F,6.5F));
        partData.addChild("front_mudguard_back_left", ModelPartBuilder.create().uv(0,53).cuboid(-0.5F, -2.5F, -1.5F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(-9.0608F,-3.4287F,6.5F));
        partData.addChild("front_mudguard_back_right", ModelPartBuilder.create().uv(0,53).cuboid(0.0F, -5.0F, -8.0F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(-10.475F,-5.55F,0.0F));
        partData.addChild("front_mudguard_center_right", ModelPartBuilder.create().uv(0,53).cuboid(-0.0707F, -4.9649F, -8.0F, 1.0F, 5.0F, 3.0F), ModelTransform.pivot(-15.425F,-5.475F,0.0F));
        partData.addChild("hood", ModelPartBuilder.create().uv(0,13).cuboid(-15.0F, -3.075F, -5.0F, 15.0F, 3.0F, 10.0F), ModelTransform.pivot(-7.0F,-6.0F,0.0F));
        return TexturedModelData.of(modelData,64,64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
