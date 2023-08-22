package io.github.foundationgames.automobility.fabric.mixin.controlify;

import dev.isxander.controlify.api.bind.ControllerBinding;
import dev.isxander.controlify.bindings.ControllerBindingImpl;
import dev.isxander.controlify.bindings.IBind;
import dev.isxander.controlify.controller.Controller;
import dev.isxander.controlify.controller.ControllerState;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.fabric.controller.controlify.ControlifyCompat;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = ControllerBindingImpl.class, remap = false)
public abstract class ControllerBindingImplMixin implements ControllerBinding {
    @Shadow @Final private Controller<?, ?> controller;

    @Shadow public abstract IBind<ControllerState> getBind();

    @Inject(method = {"held", "prevHeld"}, at = @At("HEAD"), cancellable = true)
    private void automobility$makeAutomobileInputsWorkDigital(CallbackInfoReturnable<Boolean> cir) {
        if (this.shouldHideInput()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = {"state", "prevState"}, at = @At("HEAD"), cancellable = true)
    private void automobility$makeAutomobileInputsWorkAnalogue(CallbackInfoReturnable<Float> cir) {
        if (this.shouldHideInput()) {
            cir.setReturnValue(0f);
        }
    }

    @Unique
    private boolean shouldHideInput() {
        var player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof AutomobileEntity) {
            var controller = this.controller;
            for (var supplier : ControlifyCompat.AUTOMOBILITY_BINDINGS) {
                var binding = supplier.onController(controller);
                if (binding != this && binding.getBind().equals(this.getBind())) {
                    return true;
                }
            }
        }
        return false;
    }
}
