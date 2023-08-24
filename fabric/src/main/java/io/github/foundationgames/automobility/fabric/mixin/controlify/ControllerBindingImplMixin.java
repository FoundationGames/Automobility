package io.github.foundationgames.automobility.fabric.mixin.controlify;

import dev.isxander.controlify.api.bind.ControllerBinding;
import dev.isxander.controlify.bindings.ControllerBindingImpl;
import dev.isxander.controlify.bindings.IBind;
import dev.isxander.controlify.controller.Controller;
import dev.isxander.controlify.controller.ControllerState;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.fabric.controller.controlify.ControlifyCompat;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = ControllerBindingImpl.class, remap = false)
public abstract class ControllerBindingImplMixin implements ControllerBinding {
    @Shadow @Final private Controller<?, ?> controller;

    @Shadow public abstract IBind<ControllerState> getBind();

    @Inject(method = "held", at = @At("HEAD"), cancellable = true)
    private void automobility$makeAutomobileInputsWork(CallbackInfoReturnable<Boolean> cir) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player != null && player.getVehicle() instanceof AutomobileEntity auto &&
                auto.getControllingPassenger() == player && minecraft.screen == null) {
            var controller = this.controller;
            for (var supplier : ControlifyCompat.AUTOMOBILITY_BINDINGS) {
                var binding = supplier.onController(controller);
                if (binding != this && binding.getBind().equals(this.getBind())) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "prevHeld", at = @At("HEAD"), cancellable = true)
    private void automobility$makeAutomobileInputsHaveWorked(CallbackInfoReturnable<Boolean> cir) {
        this.automobility$makeAutomobileInputsWork(cir);
    }
}
