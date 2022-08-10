package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.BaseChestRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.jetbrains.annotations.Nullable;

public class ChestRearAttachmentModel extends RearAttachmentRenderModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/rear_attachment/chest"), "main");

    private final ModelPart lid;

    public ChestRearAttachmentModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutoutNoCull, ctx, MODEL_LAYER);
        this.lid = this.root.getChild("lid");
    }

    @Override
    public void setRenderState(@Nullable RearAttachment attachment, float wheelAngle, float tickDelta) {
        super.setRenderState(attachment, wheelAngle, tickDelta);

        if (attachment instanceof BaseChestRearAttachment chest) {
            float angle = 1 - chest.lidAnimator.getProgress(tickDelta);
            angle = 1 - (angle * angle * angle);
            this.lid.setAngles((float) (angle * Math.PI * 0.5), 0, 0);
        }
    }

    @Override
    public void resetModel() {
        super.resetModel();
        this.lid.setAngles(0, 0, 0);
    }
}
