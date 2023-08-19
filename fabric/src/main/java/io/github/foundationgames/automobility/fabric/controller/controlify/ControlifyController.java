package io.github.foundationgames.automobility.fabric.controller.controlify;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.BindingSupplier;
import dev.isxander.controlify.api.bind.ControllerBinding;
import dev.isxander.controlify.rumble.BasicRumbleEffect;
import dev.isxander.controlify.rumble.ContinuousRumbleEffect;
import dev.isxander.controlify.rumble.RumbleSource;
import dev.isxander.controlify.rumble.RumbleState;
import io.github.foundationgames.automobility.controller.AutomobileController;

public class ControlifyController implements AutomobileController {
    static BindingSupplier accelerateBinding, brakeBinding, driftBinding;
    private ContinuousRumbleEffect maxChargeRumble = null, boostingRumble = null, offRoadRumble = null;

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
                            t -> new RumbleState(t < 0.25 ? 1 : 0, (1 - t) * 0.7f),
                            5
                    )
            );
        });
    }

    @Override
    public void groundThudRumble() {
        ControlifyApi.get().getCurrentController().ifPresent(controller -> {
            controller.rumbleManager().play(
                    RumbleSource.DAMAGE,
                    BasicRumbleEffect.constant(0.1f, 0.3f, 3)
            );
        });
    }

    @Override
    public void driftChargeRumble() {
        ControlifyApi.get().getCurrentController().ifPresent(controller -> {
            controller.rumbleManager().play(
                    RumbleSource.DAMAGE,
                    BasicRumbleEffect.byTime(
                            t -> new RumbleState(t < 0.15 ? 0.4f : 0, (1 - t) * 0.2f),
                            6
                    )
            );
        });
    }

    @Override
    public void updateMaxChargeRumbleState(boolean maxCharge) {
        if (maxCharge) {
            if (maxChargeRumble != null && !maxChargeRumble.isFinished())
                return;

            maxChargeRumble = ContinuousRumbleEffect.builder()
                    .constant(0f, 0.05f)
                    .build();
            ControlifyApi.get().getCurrentController().ifPresent(controller ->
                    controller.rumbleManager().play(RumbleSource.MASTER, maxChargeRumble));
        } else if (maxChargeRumble != null) {
            maxChargeRumble.stop();
        }
    }

    @Override
    public void updateBoostingRumbleState(boolean boosting, float boostPower) {
        if (boosting) {
            if (boostingRumble != null && !boostingRumble.isFinished())
                return;

            boostingRumble = ContinuousRumbleEffect.builder()
                    .constant(0.1f * boostPower, 0.5f * boostPower)
                    .build();
            ControlifyApi.get().getCurrentController().ifPresent(controller ->
                    controller.rumbleManager().play(RumbleSource.MASTER, boostingRumble));
        } else if (boostingRumble != null) {
            boostingRumble.stop();
        }
    }

    @Override
    public void updateOffRoadRumbleState(boolean inOffRoad) {
        if (inOffRoad) {
            if (offRoadRumble != null && !offRoadRumble.isFinished())
                return;

            offRoadRumble = ContinuousRumbleEffect.builder()
                    .constant(0.04f, 0.02f)
                    .build();
            ControlifyApi.get().getCurrentController().ifPresent(controller ->
                    controller.rumbleManager().play(RumbleSource.MASTER, offRoadRumble));
        } else if (offRoadRumble != null) {
            offRoadRumble.stop();
        }
    }
}
