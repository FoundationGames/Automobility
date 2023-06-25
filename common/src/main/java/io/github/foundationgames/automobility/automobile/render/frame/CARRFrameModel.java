package io.github.foundationgames.automobility.automobile.render.frame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.BaseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class CARRFrameModel extends BaseModel {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Automobility.rl("automobile/frame/c_arr"), "main");

    public CARRFrameModel(EntityRendererProvider.Context ctx) {
        super(RenderType::entityCutoutNoCull, ctx, MODEL_LAYER);
    }

    @Override
    protected void prepare(PoseStack matrices) {
        matrices.mulPose(Axis.YP.rotationDegrees(-90));
    }
}
