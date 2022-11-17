package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.intermediary.Intermediary;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow
    public Input input;

    @Inject(method = "rideTick", at = @At("TAIL"))
    public void automobility$setAutomobileInputs(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer)(Object)this;
        if (self.getVehicle() instanceof AutomobileEntity vehicle) {
            if (Intermediary.get().inControllerMode()) {
                vehicle.provideClientInput(
                        Intermediary.get().controllerAccel(),
                        Intermediary.get().controllerBrake(),
                        input.left,
                        input.right,
                        Intermediary.get().controllerDrift()
                );
            } else {
                vehicle.provideClientInput(
                        input.up,
                        input.down,
                        input.left,
                        input.right,
                        input.jumping
                );
            }
        }
    }
}
