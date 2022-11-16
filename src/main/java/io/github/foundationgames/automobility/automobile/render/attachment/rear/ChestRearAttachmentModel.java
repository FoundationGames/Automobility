package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.BaseChestRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;

public class ChestRearAttachmentModel extends RearAttachmentRenderModel {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Automobility.rl("automobile/rear_attachment/chest"), "main");

    private final ModelPart lid;

    public ChestRearAttachmentModel(EntityRendererProvider.Context ctx) {
        super(RenderType::entityCutoutNoCull, ctx, MODEL_LAYER);
        this.lid = this.root.getChild("lid");
    }

    @Override
    public void setRenderState(@Nullable RearAttachment attachment, float wheelAngle, float tickDelta) {
        super.setRenderState(attachment, wheelAngle, tickDelta);

        if (attachment instanceof BaseChestRearAttachment chest) {
            float angle = 1 - chest.lidAnimator.getOpenness(tickDelta);
            angle = 1 - (angle * angle * angle);
            this.lid.setRotation((float) (angle * Math.PI * 0.5), 0, 0);
        }
    }

    @Override
    public void resetModel() {
        super.resetModel();
        this.lid.setRotation(0, 0, 0);
    }
}
