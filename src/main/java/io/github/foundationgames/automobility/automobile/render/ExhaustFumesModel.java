package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ExhaustFumesModel extends BaseModel {
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

    public ExhaustFumesModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutout, ctx, MODEL_LAYER);
    }
}
