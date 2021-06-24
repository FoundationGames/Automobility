package io.github.foundationgames.automobility.util.lambdacontrols;

import net.fabricmc.loader.api.FabricLoader;

public enum ControllerUtils {;
    public static boolean accelerating() {
        return isLCLoaded() && AutomobilityLC.ACCELERATE.isButtonDown();
    }

    public static boolean braking() {
        return isLCLoaded() && AutomobilityLC.BRAKE.isButtonDown();
    }

    public static boolean drifting() {
        return isLCLoaded() && AutomobilityLC.DRIFT.isButtonDown();
    }

    public static boolean inControllerMode() {
        return isLCLoaded() && AutomobilityLC.IN_CONTROLLER_MODE.get();
    }

    private static boolean isLCLoaded() {
        return FabricLoader.getInstance().isModLoaded("lambdacontrols");
    }
}
