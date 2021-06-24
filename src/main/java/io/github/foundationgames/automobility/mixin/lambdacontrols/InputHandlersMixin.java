package io.github.foundationgames.automobility.mixin.lambdacontrols;

import dev.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import dev.lambdaurora.lambdacontrols.client.controller.InputHandlers;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.util.lambdacontrols.AutomobilityLC;
import net.minecraft.client.MinecraftClient;
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
    private static void automobility$makeAutomobileInputsWork(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, CallbackInfoReturnable<Boolean> cir) {
        var player = client.player;
        if (!(player == null || !(player.getVehicle() instanceof AutomobileEntity))) {
            for (ButtonBinding ab : AutomobilityLC.AUTOMOBILITY_BINDINGS) {
                if (Arrays.equals(ab.getButton(), binding.getButton())) cir.setReturnValue(false);
            }
        }
    }
}
