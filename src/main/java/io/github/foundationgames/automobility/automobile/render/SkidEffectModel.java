package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class SkidEffectModel extends BaseModel {
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
    public static final Identifier[] DEBRIS_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/skid_effect/skid_debris_0.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_debris_1.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_debris_2.png")
    };

    public SkidEffectModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout, ctx, MODEL_LAYER);
    }
}
