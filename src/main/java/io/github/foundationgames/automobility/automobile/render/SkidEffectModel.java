package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SkidEffectModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile_skid_effect"), "main");

    public static final Identifier[] SMOKE_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/skid_effect/skid_smoke_0.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_smoke_1.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_smoke_2.png")
    };
    public static final Identifier[] SPARK_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/skid_effect/skid_sparks_0.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_sparks_1.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_sparks_2.png")
    };
    public static final Identifier[] FLAME_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/skid_effect/skid_flames_0.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_flames_1.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_flames_2.png")
    };

    private final ModelPart main;
    private final ModelPart side;
    private final ModelPart rear;

    public SkidEffectModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEyes);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
        this.rear = this.main.getChild("rear");
        this.side = this.main.getChild("side");
        setRotationAngle(side, 0.1745F, 0.0F, 0.2182F);
        setRotationAngle(rear, 0.0F, 0.0F, 0.5236F);
    }

    private void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData partData = root.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F,24.0F,0.0F));
        partData.addChild("side", ModelPartBuilder.create().uv(0,4).cuboid(0.0F, -8.675F, -4.0F, 4.0F, 8.0F, 0.0F), ModelTransform.pivot(-4.0F,0.0F,4.0F));
        partData.addChild("rear", ModelPartBuilder.create().uv(0,0).cuboid(0.0F, -7.0F, -4.0F, 0.0F, 8.0F, 4.0F), ModelTransform.pivot(0.0F,0.0F,4.0F));
        return TexturedModelData.of(modelData,16,16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
