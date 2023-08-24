package io.github.foundationgames.automobility.fabric.mixin.midnightcontrols;

import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputHandlers;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.fabric.controller.midnightcontrols.MidnightController;
import io.github.foundationgames.automobility.platform.Platform;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Pseudo
@Mixin(value = InputHandlers.class, remap = false)
public class InputHandlersMixin {
    @Inject(method = "inGame", at = @At("HEAD"), cancellable = true)
    private static void automobility$makeAutomobileInputsWork(@NotNull Minecraft client, @NotNull ButtonBinding binding, CallbackInfoReturnable<Boolean> cir) {
        var player = client.player;
        if (player != null  && player.getVehicle() instanceof AutomobileEntity auto &&
                auto.getControllingPassenger() == player && client.screen == null) {
            for (ButtonBinding ab : ((MidnightController) Platform.get().controller()).AUTOMOBILITY_BINDINGS) {
                if (Arrays.equals(ab.getButton(), binding.getButton())) cir.setReturnValue(false);
            }
        }
    }
}
