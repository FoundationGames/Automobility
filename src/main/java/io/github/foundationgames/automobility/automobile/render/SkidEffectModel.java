package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SkidEffectModel extends BaseModel {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Automobility.rl("automobile_skid_effect"), "main");

    public static final ResourceLocation[] COOL_SPARK_TEXTURES = new ResourceLocation[] {
            Automobility.rl("textures/entity/automobile/skid_effect/skid_cool_sparks_0.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_cool_sparks_1.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_cool_sparks_2.png")
    };
    public static final ResourceLocation[] HOT_SPARK_TEXTURES = new ResourceLocation[] {
            Automobility.rl("textures/entity/automobile/skid_effect/skid_hot_sparks_0.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_hot_sparks_1.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_hot_sparks_2.png")
    };
    public static final ResourceLocation[] FLAME_TEXTURES = new ResourceLocation[] {
            Automobility.rl("textures/entity/automobile/skid_effect/skid_flames_0.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_flames_1.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_flames_2.png")
    };
    public static final ResourceLocation[] DEBRIS_TEXTURES = new ResourceLocation[] {
            Automobility.rl("textures/entity/automobile/skid_effect/skid_debris_0.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_debris_1.png"),
            Automobility.rl("textures/entity/automobile/skid_effect/skid_debris_2.png")
    };

    public SkidEffectModel(EntityRendererProvider.Context ctx) {
        super(RenderType::entityCutout, ctx, MODEL_LAYER);
    }
}
