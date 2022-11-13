package io.github.foundationgames.automobility.automobile.render.attachment.rear;

import com.mojang.datafixers.util.Pair;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.BannerPostRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BannerPostRearAttachmentModel extends RearAttachmentRenderModel {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Automobility.id("automobile/rear_attachment/banner_post"), "main");

    private final ModelPart fakePole;
    private final ModelPart pole;
    private final ModelPart bar;

    private final ModelPart flagPole;
    private final ModelPart flagBar;
    private final ModelPart flag;

    private boolean renderPole;
    private boolean renderFlag;
    private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;

    public BannerPostRearAttachmentModel(EntityRendererFactory.Context ctx) {
        super(RenderLayer::getEntityCutoutNoCull, ctx, MODEL_LAYER);

        this.fakePole = this.root.getChild("fake_pole");
        this.pole = this.root.getChild("pole");
        this.bar = this.pole.getChild("bar");

        this.flagPole = this.root.getChild("flag_pole");
        this.flagBar = this.flagPole.getChild("flag_bar");
        this.flag = this.flagBar.getChild("flag");

        this.flagPole.visible = false;
        this.pole.visible = false;
    }

    @Override
    public void setRenderState(@Nullable RearAttachment attachment, float wheelAngle, float tickDelta) {
        super.setRenderState(attachment, wheelAngle, tickDelta);

        float push = attachment == null ? 0 : (float) Math.pow(Math.max(0, attachment.automobile().getHSpeed() * 0.368f), 2);
        this.pole.pitch = -push;
        this.bar.pitch = push;
        this.flagPole.pitch = -push;
        this.flagBar.pitch = push;

        if (attachment instanceof BannerPostRearAttachment bannerPost) {
            this.renderFlag = bannerPost.getBaseColor() != null;
            this.patterns = bannerPost.getPatterns();

            this.flag.setAngles(push, this.flag.yaw, 0.05f * (float)Math.sin((attachment.automobile().getTime() + tickDelta) / 20));
        }

        this.renderPole = attachment != null;
        this.fakePole.visible = false;
    }

    @Override
    public void renderExtra(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.renderPole) {
            this.pole.visible = true;
            matrices.push();

            matrices.translate(0, -1f, 0);
            matrices.scale(0.666f, 0.666f, 0.666f);
            matrices.translate(0, 1f, 0);
            this.pole.render(matrices, vertices, light, overlay, red, green, blue, alpha);

            matrices.pop();
            this.pole.visible = false;
            this.renderPole = false;
        }
        this.fakePole.visible = true;
    }

    @Override
    public void renderOtherLayer(MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay) {
        if (this.renderFlag) {
            this.flagPole.visible = true;
            matrices.push();

            matrices.translate(0, -1f, 0);
            matrices.scale(0.666f, 0.666f, 0.666f);
            matrices.translate(0, 1f, 0);
            BannerBlockEntityRenderer.renderCanvas(matrices, consumers, light, overlay, this.flagPole, ModelLoader.BANNER_BASE, true, this.patterns, false);

            matrices.pop();
            this.flagPole.visible = false;
            this.renderFlag = false;
        }
    }
}
