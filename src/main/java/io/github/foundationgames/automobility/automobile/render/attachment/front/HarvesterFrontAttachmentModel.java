package io.github.foundationgames.automobility.automobile.render.attachment.front;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.front.FrontAttachment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;

public class HarvesterFrontAttachmentModel extends FrontAttachmentRenderModel {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Automobility.rl("automobile/front_attachment/harvester"), "main");

    private final @Nullable ModelPart roller;

    public HarvesterFrontAttachmentModel(EntityRendererProvider.Context ctx) {
        super(RenderType::entityCutout, ctx, MODEL_LAYER);

        if (this.ground != null) {
            this.roller = this.ground.getChild("roller");
        } else {
            this.roller = null;
        }
    }

    @Override
    public void setRenderState(@Nullable FrontAttachment attachment, float groundHeight, float tickDelta) {
        super.setRenderState(attachment, groundHeight, tickDelta);

        if (this.roller != null) {
            if (attachment != null) {
                this.roller.setRotation((float) Math.toRadians(attachment.automobile().getWheelAngle(tickDelta)), 0, 0);
            } else {
                this.roller.setRotation(0, 0, 0);
            }
        }
    }
}
