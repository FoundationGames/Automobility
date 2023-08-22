package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.platform.Platform;
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
            if (Platform.get().controller().inControllerMode()) {
                vehicle.provideClientInput(
                        Platform.get().controller().acceleration() - Platform.get().controller().brakeForce(),
                        input.leftImpulse,
                        Platform.get().controller().drifting()
                );
            } else {
                vehicle.provideClientInput(
                        input.forwardImpulse,
                        input.leftImpulse,
                        input.jumping
                );
            }
        }
    }
}
