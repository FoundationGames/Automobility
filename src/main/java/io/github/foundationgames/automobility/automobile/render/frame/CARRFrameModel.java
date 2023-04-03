package io.github.foundationgames.automobility.automobile.render.frame;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class CARRFrameModel extends BaseModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/frame/c_arr"), "main");

    public CARRFrameModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutoutNoCull, ctx, MODEL_LAYER);
    }

    @Override
    protected void prepare(MatrixStack matrices) {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
    }
}
