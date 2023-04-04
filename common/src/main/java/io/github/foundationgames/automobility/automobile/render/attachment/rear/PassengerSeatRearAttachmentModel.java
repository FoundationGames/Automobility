package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PassengerSeatRearAttachmentModel extends RearAttachmentRenderModel {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Automobility.rl("automobile/rear_attachment/passenger_seat"), "main");

    public PassengerSeatRearAttachmentModel(EntityRendererProvider.Context ctx) {
        super(RenderType::entityCutoutNoCull, ctx, MODEL_LAYER);
    }
}
