package io.github.foundationgames.automobility.fabric.controller.midnightcontrols;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.CompatHandler;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.controller.AutomobileController;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.Minecraft;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.function.PairPredicate;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class MidnightController implements CompatHandler, AutomobileController {
    public final PairPredicate<Minecraft, ButtonBinding> ON_AUTOMOBILE = (client, button) -> client.player != null && client.player.getVehicle() instanceof AutomobileEntity;

    public final Set<ButtonBinding> AUTOMOBILITY_BINDINGS = new HashSet<>();

    public final ButtonBinding ACCELERATE = binding(new ButtonBinding.Builder(Automobility.rl("accelerate_automobile"))
            .buttons(GLFW_GAMEPAD_BUTTON_A).filter(ON_AUTOMOBILE).register());

    public final ButtonBinding BRAKE = binding(new ButtonBinding.Builder(Automobility.rl("brake_automobile"))
            .buttons(GLFW_GAMEPAD_BUTTON_B).filter(ON_AUTOMOBILE).register());

    public final ButtonBinding DRIFT = binding(new ButtonBinding.Builder(Automobility.rl("drift_automobile"))
            .buttons(ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true)).filter(ON_AUTOMOBILE).register());

    public final ButtonCategory AUTOMOBILITY_CATEGORY = InputManager.registerCategory(new Identifier(Automobility.MOD_ID, "automobility"));

    public Supplier<Boolean> IN_CONTROLLER_MODE = () -> false;

    @Override
    public void initCompat() {
        MidnightControlsCompat.registerCompatHandler(new MidnightController());
    }

    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        AUTOMOBILITY_CATEGORY.registerAllBindings(ACCELERATE, BRAKE, DRIFT);
        IN_CONTROLLER_MODE = () -> MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER;
    }

    private ButtonBinding binding(ButtonBinding binding) {
        AUTOMOBILITY_BINDINGS.add(binding);
        return binding;
    }

    @Override
    public boolean accelerating() {
        return ACCELERATE.isButtonDown();
    }

    @Override
    public boolean braking() {
        return BRAKE.isButtonDown();
    }

    @Override
    public boolean drifting() {
        return DRIFT.isButtonDown();
    }

    @Override
    public boolean inControllerMode() {
        return IN_CONTROLLER_MODE.get();
    }
}
