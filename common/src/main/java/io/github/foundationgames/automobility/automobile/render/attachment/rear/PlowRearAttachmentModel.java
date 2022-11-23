package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.ExtendableRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

public class PlowRearAttachmentModel extends RearAttachmentRenderModel {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Automobility.rl("automobile/rear_attachment/plow"), "main");

    private final ModelPart assembly;
    private final ModelPart instrument;

    public PlowRearAttachmentModel(EntityRendererProvider.Context ctx) {
        super(RenderType::entityCutoutNoCull, ctx, MODEL_LAYER);

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
            this.assembly.setRotation(6.5f * anim, 0, 0);
            this.instrument.setRotation(-3 * anim, 0, 0);
        }
    }
}
