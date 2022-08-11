package io.github.foundationgames.automobility.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.midnightcontrols.ControllerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;
import java.util.function.Function;

public enum AutomobileHud {;
    public static final List<ControlHint> CONTROL_HINTS = Lists.newArrayList(
            new ControlHint("accelerate", options -> options.forwardKey),
            new ControlHint("brake", options -> options.backKey),
            new ControlHint("steer_left", options -> options.leftKey),
            new ControlHint("steer_right", options -> options.rightKey),
            new ControlHint("drift", options -> options.jumpKey)
    );

    public static void render(MatrixStack matrices, PlayerEntity player, AutomobileEntity auto, float tickDelta) {
        renderSpeedometer(matrices, auto);

        if (!ControllerUtils.inControllerMode()) {
            float alpha = Math.max(0, (auto.getStandStillTime() * 2) - 1);
            if (alpha > 0) {
                renderControlHints(matrices, alpha);
            }
        }
    }

    private static void renderSpeedometer(MatrixStack matrices, AutomobileEntity auto) {
        float speed = Math.abs(auto.getHSpeed() * 20);
        int color = 0xFFFFFF;
        if (auto.getBoostTimer() > 0) color = 0xFF6F00;
        if (auto.getTurboCharge() > AutomobileEntity.SMALL_TURBO_TIME) color = 0xFFEA4A;
        if (auto.getTurboCharge() > AutomobileEntity.MEDIUM_TURBO_TIME) color = 0x7DE9FF;
        if (auto.getTurboCharge() > AutomobileEntity.LARGE_TURBO_TIME) color = 0x906EFF;
        DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, new LiteralText(AUtils.DEC_TWO_PLACES.format(speed) +" m/s"), 20, 20, color);
    }

    private static void renderControlHints(MatrixStack matrices, float alpha) {
        int x = 20;
        int y = 50;
        var options = MinecraftClient.getInstance().options;
        var font = MinecraftClient.getInstance().textRenderer;

        for (var control : CONTROL_HINTS) {
            var keyTxt = control.getKeybindText(options);
            int keyTxtWid = font.getWidth(keyTxt);

            DrawableHelper.fill(matrices, x, y, x + keyTxtWid + 6, y + 14, ((int)(alpha * 0xAB) << 24));

            int textColor = 0x00FFFFFF | ((int)(alpha * 0xFF) << 24);
            DrawableHelper.drawTextWithShadow(matrices, font, keyTxt, x + 3, y + 3, textColor);
            DrawableHelper.drawTextWithShadow(matrices, font, control.getText(), x + keyTxtWid + 9, y + 3, textColor);

            y += 17;
        }
    }

    public record ControlHint(String name, Function<GameOptions, KeyBinding> keybind) {
        public Text getText() {
            return new TranslatableText("automobile_control."+name);
        }

        public Text getKeybindText(GameOptions options) {
            return keybind.apply(options).getBoundKeyLocalizedText();
        }
    }
}
