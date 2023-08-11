package io.github.foundationgames.automobility.screen;

import com.google.common.collect.Lists;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Function;

public enum AutomobileHud {;
    public static final List<ControlHint> CONTROL_HINTS = Lists.newArrayList(
            new ControlHint("accelerate", options -> options.keyUp),
            new ControlHint("brake", options -> options.keyDown),
            new ControlHint("steer_left", options -> options.keyLeft),
            new ControlHint("steer_right", options -> options.keyRight),
            new ControlHint("drift", options -> options.keyJump)
    );

    public static void render(GuiGraphics graphics, Player player, AutomobileEntity auto, float tickDelta) {
        renderSpeedometer(graphics, auto);

        if (!Platform.get().controller().inControllerMode()) {
            float alpha = Math.max(0, (auto.getStandStillTime() * 2) - 1);

            // Check on a 0-255 converted version of alpha, because 0 alpha will render things at 100% alpha for some
            // reason, and small enough numbers (which would result in 0 alpha as an int, but non zero as a float) would
            // result in a brief tick of 100% alpha, messing up the smoothness of the fade in animation
            if ((int)(alpha * 0xFF) > 0) {
                renderControlHints(graphics, alpha);
            }
        }
    }

    private static void renderSpeedometer(GuiGraphics graphics, AutomobileEntity auto) {
        float speed = (float) auto.getEffectiveSpeed() * 20;
        int color = 0xFFFFFF;
        if (auto.getBoostTimer() > 0) color = 0xFF6F00;
        if (auto.getTurboCharge() > AutomobileEntity.SMALL_TURBO_TIME) color = 0xFFEA4A;
        if (auto.getTurboCharge() > AutomobileEntity.MEDIUM_TURBO_TIME) color = 0x7DE9FF;
        if (auto.getTurboCharge() > AutomobileEntity.LARGE_TURBO_TIME) color = 0x906EFF;
        graphics.drawString(Minecraft.getInstance().font, Component.literal(AUtils.DEC_TWO_PLACES.format(speed) +" m/s"), 20, 20, color);
    }

    private static void renderControlHints(GuiGraphics graphics, float alpha) {
        int x = 20;
        int y = 50;
        var options = Minecraft.getInstance().options;
        var font = Minecraft.getInstance().font;

        for (var control : CONTROL_HINTS) {
            var keyTxt = control.getKeybindText(options);
            int keyTxtWid = font.width(keyTxt);

            graphics.fill(x, y, x + keyTxtWid + 6, y + 14, ((int)(alpha * 0xAB) << 24));

            int textColor = 0x00FFFFFF | ((int)(alpha * 0xFF) << 24);
            graphics.drawString(font, keyTxt, x + 3, y + 3, textColor);
            graphics.drawString(font, control.getText(), x + keyTxtWid + 9, y + 3, textColor);

            y += 17;
        }
    }

    public record ControlHint(String name, Function<Options, KeyMapping> keybind) {
        public Component getText() {
            return Component.translatable("automobile_control."+name);
        }

        public Component getKeybindText(Options options) {
            return keybind.apply(options).getTranslatedKeyMessage();
        }
    }
}
