package io.github.foundationgames.automobility.util.lambdacontrols;

import dev.lambdaurora.lambdacontrols.ControlsMode;
import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.compat.CompatHandler;
import dev.lambdaurora.lambdacontrols.client.compat.LambdaControlsCompat;
import dev.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import dev.lambdaurora.lambdacontrols.client.controller.ButtonCategory;
import dev.lambdaurora.lambdacontrols.client.controller.InputManager;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.MinecraftClient;

// WHAT
import org.aperlambda.lambdacommon.Identifier;
// WHY

import org.aperlambda.lambdacommon.utils.function.PairPredicate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

/*
 * Don't access this class directly in other packages, it will likely throw a fit if LambdaControls isn't loaded
 */
public class AutomobilityLC implements CompatHandler {
    private static final PairPredicate<MinecraftClient, ButtonBinding> ON_AUTOMOBILE = (client, button) -> client.player != null && client.player.getVehicle() instanceof AutomobileEntity;

    public static final ButtonBinding ACCELERATE = new ButtonBinding.Builder(Automobility.id("accelerate_automobile"))
            .buttons(GLFW_GAMEPAD_BUTTON_A).filter(ON_AUTOMOBILE).register();

    public static final ButtonBinding BRAKE = new ButtonBinding.Builder(Automobility.id("brake_automobile"))
            .buttons(GLFW_GAMEPAD_BUTTON_B).filter(ON_AUTOMOBILE).register();

    public static final ButtonBinding DRIFT = new ButtonBinding.Builder(Automobility.id("drift_automobile"))
            .buttons(ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true)).filter(ON_AUTOMOBILE).register();

    //                                                                                       There is 1 impostor among us
    public static final ButtonCategory AUTOMOBILITY_CATEGORY = InputManager.registerCategory(new Identifier(Automobility.MOD_ID, "automobility"));

    public static Supplier<Boolean> IN_CONTROLLER_MODE = () -> false;

    public static void init() {
        LambdaControlsCompat.registerCompatHandler(new AutomobilityLC());
    }

    @Override
    public void handle(@NotNull LambdaControlsClient mod) {
        AUTOMOBILITY_CATEGORY.registerAllBindings(ACCELERATE, BRAKE, DRIFT);
        IN_CONTROLLER_MODE = () -> mod.config.getControlsMode() == ControlsMode.CONTROLLER;
    }
}
