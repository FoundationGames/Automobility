package io.github.foundationgames.automobility.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        this.renderBackground(graphics);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
