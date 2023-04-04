package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ExhaustFumesModel extends BaseModel {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Automobility.rl("automobile_exhaust_fumes"), "main");

    public static final ResourceLocation[] SMOKE_TEXTURES = new ResourceLocation[] {
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_smoke_0.png"),
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_smoke_1.png"),
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_smoke_2.png"),
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_smoke_3.png")
    };
    public static final ResourceLocation[] FLAME_TEXTURES = new ResourceLocation[] {
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_flames_0.png"),
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_flames_1.png"),
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_flames_2.png"),
            Automobility.rl("textures/entity/automobile/exhaust/exhaust_flames_3.png")
    };

    public ExhaustFumesModel(EntityRendererProvider.Context ctx) {
        super(RenderType::entityCutout, ctx, MODEL_LAYER);
    }
}
