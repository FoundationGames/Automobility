package io.github.foundationgames.automobility.automobile.render.frame;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class TractorFrameModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/frame/tractor"), "main");

    private final ModelPart main;
    private final ModelPart seat_back;
    private final ModelPart pipe_tip;
    private final ModelPart dashboard;

    public TractorFrameModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.dashboard = this.main.getChild("dashboard");
        this.pipe_tip = this.main.getChild("pipe_tip");
        this.seat_back = this.main.getChild("seat_back");
        setRotationAngle(seat_back, -1.8326F, 0.0F, 0.0F);
        setRotationAngle(pipe_tip, -0.1309F, 0.0F, 0.0F);
        setRotationAngle(dashboard, 0.5672F, 0.0F, 0.0F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-4.0F, -13.0F, -19.0F, 8.0F, 9.0F, 15.0F).uv(0,24).cuboid(-7.0F, -8.0F, -4.0F, 14.0F, 7.0F, 12.0F).uv(31,9).cuboid(-1.0F, -17.0F, -16.0F, 2.0F, 4.0F, 2.0F).uv(0,0).cuboid(-1.0F, -4.0F, -16.0F, 2.0F, 5.0F, 2.0F).uv(0,43).cuboid(-3.0F, -9.0F, -3.0F, 6.0F, 1.0F, 7.0F).uv(26,43).cuboid(-5.0F, -8.0F, 8.0F, 10.0F, 6.0F, 4.0F).uv(0,0).cuboid(-1.0F, -9.0F, 5.0F, 1.0F, 1.0F, 1.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("seat_back", ModelPartBuilder.create().uv(0,43).cuboid(-3.0F, -1.0F, -7.0F, 6.0F, 1.0F, 7.0F), ModelTransform.pivot(0.0F,-9.0F,3.0F));
        partData.addChild("pipe_tip", ModelPartBuilder.create().uv(31,0).cuboid(-0.5F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F,-17.0F,-15.5F));
        partData.addChild("dashboard", ModelPartBuilder.create().uv(31,0).cuboid(-2.0F, -3.0F, -5.0F, 4.0F, 3.0F, 5.0F), ModelTransform.pivot(0.0F,-13.0F,-4.0F));
        return TexturedModelData.of(modelData,64,64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
