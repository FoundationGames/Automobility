package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.LiteralText;

public class AutomobilityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AutomobilityBlocks.initClient();
        AutomobilityItems.initClient();
        AutomobilityEntities.initClient();
        PayloadPackets.initClient();

        AutomobilityAssets.setup();

        HudRenderCallback.EVENT.register((matrices, delta) -> {
            var player = MinecraftClient.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                float speed = Math.abs(auto.getHSpeed() * 20);
                int color = 0xFFFFFF;
                if (auto.getBoostTimer() > 0) color = 0xFF6F00;
                if (auto.getDriftTimer() > AutomobileEntity.DRIFT_TURBO_TIME) color = 0x7DE9FF;
                DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, new LiteralText(AUtils.DEC_TWO_PLACES.format(speed) +" m/s"), 20, 20, color);
            }
        });
    }
}
