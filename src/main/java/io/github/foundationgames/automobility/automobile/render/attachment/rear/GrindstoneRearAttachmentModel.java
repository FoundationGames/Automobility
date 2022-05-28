package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.jetbrains.annotations.Nullable;

public class GrindstoneRearAttachmentModel extends RearAttachmentRenderModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/rear_attachment/grindstone"), "main");

    private final ModelPart grindstone;

    public GrindstoneRearAttachmentModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutoutNoCull, ctx, MODEL_LAYER);
        this.grindstone = this.root.getChild("grindstone");
    }

    @Override
    public void setRenderState(@Nullable RearAttachment attachment, float wheelAngle, float tickDelta) {
        super.setRenderState(attachment, wheelAngle, tickDelta);

        if (this.grindstone != null) {
            this.grindstone.setAngles(wheelAngle * 0.25f, 0, 0);
        }
    }
}
