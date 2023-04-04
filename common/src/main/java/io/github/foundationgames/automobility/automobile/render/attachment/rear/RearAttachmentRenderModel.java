package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class RearAttachmentRenderModel extends BaseModel {
    private final @Nullable ModelPart wheels;

    public RearAttachmentRenderModel(Function<ResourceLocation, RenderType> layerFactory, EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
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
            this.wheels.setRotation(wheelAngle, 0, 0);
        }
    }

    public void resetModel() {
        this.setRenderState(null, 0, 0);
    }
}
