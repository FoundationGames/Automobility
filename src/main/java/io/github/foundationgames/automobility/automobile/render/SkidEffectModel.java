package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class SkidEffectModel extends BaseModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile_skid_effect"), "main");

    public static final Identifier[] COOL_SPARK_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/skid_effect/skid_cool_sparks_0.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_cool_sparks_1.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_cool_sparks_2.png")
    };
    public static final Identifier[] HOT_SPARK_TEXTURES = new Identifier[] {
            Automobility.id("textures/entity/automobile/skid_effect/skid_hot_sparks_0.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_hot_sparks_1.png"),
            Automobility.id("textures/entity/automobile/skid_effect/skid_hot_sparks_2.png")
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
