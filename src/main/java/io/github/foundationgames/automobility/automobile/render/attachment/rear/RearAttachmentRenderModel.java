package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class RearAttachmentRenderModel extends BaseModel {
    private final @Nullable ModelPart wheels;

    public RearAttachmentRenderModel(Function<Identifier, RenderLayer> layerFactory, EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(layerFactory, ctx, layer);
        ModelPart wheels;
        try {
            wheels = this.root.getChild("wheels");
        } catch (NoSuchElementException ignored) {
            wheels = null;
        }
        this.wheels = wheels;
    }

    public void setRenderState(@Nullable RearAttachment attachment, float wheelAngle, float tickDelta) {
        if (this.wheels != null) {
            this.wheels.setAngles(wheelAngle, 0, 0);
        }
    }

    public void resetModel() {
        this.setRenderState(null, 0, 0);
    }
}
