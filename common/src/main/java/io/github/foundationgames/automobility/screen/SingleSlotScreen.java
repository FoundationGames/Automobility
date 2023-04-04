package io.github.foundationgames.automobility.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SingleSlotScreen extends AbstractContainerScreen<SingleSlotScreenHandler> implements MenuAccess<SingleSlotScreenHandler> {
    private static final ResourceLocation TEXTURE = Automobility.rl("textures/gui/container/single_slot.png");

    public SingleSlotScreen(SingleSlotScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);

        this.imageHeight = 140;
        this.inventoryLabelY = 47;
        this.titleLabelX = 60;
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.renderTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
