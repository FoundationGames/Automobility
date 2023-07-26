package io.github.foundationgames.automobility.fabric.controller.controlify;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.BindingSupplier;
import dev.isxander.controlify.api.bind.ControllerBinding;
import dev.isxander.controlify.rumble.BasicRumbleEffect;
import dev.isxander.controlify.rumble.ContinuousRumbleEffect;
import dev.isxander.controlify.rumble.RumbleSource;
import dev.isxander.controlify.rumble.RumbleState;
import io.github.foundationgames.automobility.controller.ControllerCompat;

public class ControlifyCompat implements ControllerCompat {
    static BindingSupplier accelerateBinding, brakeBinding, driftBinding;
    private ContinuousRumbleEffect driftRumbleEffect = null;

    @Override
    public boolean accelerating() {
        return isDown(accelerateBinding);
    }

    @Override
    public boolean braking() {
        return isDown(brakeBinding);
    }

    @Override
    public boolean drifting() {
        return isDown(driftBinding);
    }

    @Override
    public boolean inControllerMode() {
        return ControlifyApi.get().currentInputMode().isController();
    }

    private boolean isDown(BindingSupplier binding) {
        return inControllerMode() && ControlifyApi.get().getCurrentController()
                .map(binding::onController)
                .map(ControllerBinding::held)
                .orElse(false);
    }

    @Override
    public void crashRumble() {
        ControlifyApi.get().getCurrentController().ifPresent(controller -> {
            controller.rumbleManager().play(
                    RumbleSource.DAMAGE,
                    BasicRumbleEffect.byTime(
                            t -> new RumbleState(t < 0.5 ? 1 : 0, 1 - t),
                            20
                    )
            );
        });
    }

    @Override
    public void updateDriftRumbleState(boolean drifting) {
        if (drifting) {
            if (driftRumbleEffect != null && !driftRumbleEffect.isFinished())
                return;

            driftRumbleEffect = ContinuousRumbleEffect.builder()
                    .constant(0f, 0.8f)
                    .build();
            ControlifyApi.get().getCurrentController().ifPresent(controller ->
                    controller.rumbleManager().play(RumbleSource.MASTER, driftRumbleEffect));
        } else if (driftRumbleEffect != null) {
            driftRumbleEffect.stop();
        }
    }
}
