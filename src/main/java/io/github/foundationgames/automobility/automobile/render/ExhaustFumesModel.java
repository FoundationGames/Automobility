package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ExhaustFumesModel extends Model {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile_exhaust_fumes"), "main");

    public static final Identifier[] SMOKE_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/exhaust/exhaust_smoke_0.png"),
            Automobility.id("textures/entity/automobile/exhaust/exhaust_smoke_1.png"),
            Automobility.id("textures/entity/automobile/exhaust/exhaust_smoke_2.png"),
            Automobility.id("textures/entity/automobile/exhaust/exhaust_smoke_3.png")
    };
    public static final Identifier[] FLAME_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/exhaust/exhaust_flames_0.png"),
            Automobility.id("textures/entity/automobile/exhaust/exhaust_flames_1.png"),
            Automobility.id("textures/entity/automobile/exhaust/exhaust_flames_2.png"),
            Automobility.id("textures/entity/automobile/exhaust/exhaust_flames_3.png")
    };

    private final ModelPart main;

    public ExhaustFumesModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEyes);
        this.main = ctx.getPart(MODEL_LAYER).getChild("main");
    }

    public static TexturedModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild("main", ModelPartBuilder.create().uv(0,0).cuboid(-1.0F, -14.0F, -1.0F, 2.0F, 14.0F, 2.0F).uv(0,13).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 0.0F, 2.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        return TexturedModelData.of(modelData,16,16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
