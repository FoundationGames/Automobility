package io.github.foundationgames.automobility.fabric.util.midnightcontrols;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.CompatHandler;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.function.PairPredicate;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;

import static org.lwjgl.glfw.GLFW.*;

public class AutomobilityMidnightControls implements CompatHandler {
    public static final PairPredicate<Minecraft, ButtonBinding> ON_AUTOMOBILE = (client, button) -> client.player != null && client.player.getVehicle() instanceof AutomobileEntity;

    public static final Set<ButtonBinding> AUTOMOBILITY_BINDINGS = new HashSet<>();

    public static final ButtonBinding ACCELERATE = binding(new ButtonBinding.Builder(Automobility.rl("accelerate_automobile"))
            .buttons(GLFW_GAMEPAD_BUTTON_A).filter(ON_AUTOMOBILE).register());

    public static final ButtonBinding BRAKE = binding(new ButtonBinding.Builder(Automobility.rl("brake_automobile"))
            .buttons(GLFW_GAMEPAD_BUTTON_B).filter(ON_AUTOMOBILE).register());

    public static final ButtonBinding DRIFT = binding(new ButtonBinding.Builder(Automobility.rl("drift_automobile"))
            .buttons(ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true)).filter(ON_AUTOMOBILE).register());

    public static final ButtonCategory AUTOMOBILITY_CATEGORY = InputManager.registerCategory(new Identifier(Automobility.MOD_ID, "automobility"));

    public static Supplier<Boolean> IN_CONTROLLER_MODE = () -> false;

    public static void init() {
        MidnightControlsCompat.registerCompatHandler(new AutomobilityMidnightControls());
    }

    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        AUTOMOBILITY_CATEGORY.registerAllBindings(ACCELERATE, BRAKE, DRIFT);
        IN_CONTROLLER_MODE = () -> MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER;
    }

    private static ButtonBinding binding(ButtonBinding binding) {
        AUTOMOBILITY_BINDINGS.add(binding);
        return binding;
    }
}
