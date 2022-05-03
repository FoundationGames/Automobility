package io.github.foundationgames.automobility.automobile.render.frame;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class MotorcarFrameModel extends BaseModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/frame/motorcar"), "main");

    public MotorcarFrameModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityTranslucentCull, ctx, MODEL_LAYER);
    }
}
