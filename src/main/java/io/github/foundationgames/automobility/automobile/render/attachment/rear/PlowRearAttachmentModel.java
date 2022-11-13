package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.ExtendableRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

public class PlowRearAttachmentModel extends RearAttachmentRenderModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/rear_attachment/plow"), "main");

    private final ModelPart assembly;
    private final ModelPart instrument;

    public PlowRearAttachmentModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutoutNoCull, ctx, MODEL_LAYER);

        ModelPart assembly = null, instrument = null;
        try {
            assembly = this.root.getChild("assembly");
            instrument = assembly.getChild("instrument");
        } catch (NoSuchElementException ignored) {}

        this.assembly = assembly;
        this.instrument = instrument;
    }

    @Override
    public void setRenderState(@Nullable RearAttachment attachment, float wheelAngle, float tickDelta) {
        super.setRenderState(attachment, wheelAngle, tickDelta);

        if (this.assembly != null && this.instrument != null && attachment instanceof ExtendableRearAttachment att) {
            float anim = att.extendAnimation(tickDelta);
            this.assembly.setAngles(6.5f * anim, 0, 0);
            this.instrument.setAngles(-3 * anim, 0, 0);
        }
    }
}
