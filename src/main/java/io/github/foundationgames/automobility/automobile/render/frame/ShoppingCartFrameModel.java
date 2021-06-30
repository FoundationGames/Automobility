package io.github.foundationgames.automobility.automobile.render.frame;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class ShoppingCartFrameModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/frame/shopping_cart"), "main");

    private final ModelPart main;
    private final ModelPart basket_back;
    private final ModelPart basket_front;
    private final ModelPart basket_support;

    public ShoppingCartFrameModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.basket_support = this.main.getChild("basket_support");
        this.basket_front = this.main.getChild("basket_front");
        this.basket_back = this.main.getChild("basket_back");
        setRotationAngle(basket_back, -0.4363F, 0.0F, 0.0F);
        setRotationAngle(basket_front, 0.1484F, 0.0F, 0.0F);
        setRotationAngle(basket_support, 0.3054F, 0.0F, 0.0F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-6.0F, -4.0F, -11.0F, 12.0F, 4.0F, 22.0F).uv(64,0).cuboid(-6.0F, -11.0F, -11.0F, 12.0F, 1.0F, 20.0F).uv(80,21).cuboid(-6.0F, -18.0F, -11.0F, 1.0F, 7.0F, 23.0F).uv(80,21).cuboid(5.0F, -18.0F, -11.0F, 1.0F, 7.0F, 23.0F).uv(75,33).cuboid(-6.0F, -20.95F, 12.675F, 12.0F, 1.0F, 2.0F).uv(0,26).cuboid(-4.0F, -17.0F, 2.75F, 8.0F, 1.0F, 9.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("basket_back", ModelPartBuilder.create().uv(77,21).cuboid(-6.0F, -11.0F, 0.0F, 12.0F, 11.0F, 1.0F), ModelTransform.pivot(0.0F,-11.0F,8.0F));
        partData.addChild("basket_front", ModelPartBuilder.create().uv(46,8).cuboid(-6.0F, 0.0F, -1.0F, 12.0F, 7.0F, 1.0F), ModelTransform.pivot(0.0F,-18.0F,-11.0F));
        partData.addChild("basket_support", ModelPartBuilder.create().uv(46,0).cuboid(-6.0F, -7.0F, -1.0F, 12.0F, 7.0F, 1.0F), ModelTransform.pivot(0.0F,-4.0F,11.0F));
        return TexturedModelData.of(modelData,128,64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}
