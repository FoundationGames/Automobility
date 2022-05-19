package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class StonecutterRearAttachmentModel extends RearAttachmentRenderModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/rear_attachment/stonecutter"), "main");

    private final ModelPart blade;

    public StonecutterRearAttachmentModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutoutNoCull, ctx, MODEL_LAYER);
        this.blade = this.root.getChild("blade");
    }

    @Override
    public void setWheelAngle(float angle) {
        super.setWheelAngle(angle);

        if (this.blade != null) {
            this.blade.setAngles(angle * 0.45f, 0, 0);
        }
    }
}
