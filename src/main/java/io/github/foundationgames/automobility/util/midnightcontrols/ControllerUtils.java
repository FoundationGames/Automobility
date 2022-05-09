package io.github.foundationgames.automobility.util.midnightcontrols;

import net.fabricmc.loader.api.FabricLoader;

public enum ControllerUtils {;
    public static boolean accelerating() {
        return isMCLoaded() && AutomobilityMidnightControls.ACCELERATE.isButtonDown();
    }

    public static boolean braking() {
        return isMCLoaded() && AutomobilityMidnightControls.BRAKE.isButtonDown();
    }

    public static boolean drifting() {
        return isMCLoaded() && AutomobilityMidnightControls.DRIFT.isButtonDown();
    }

    public static boolean inControllerMode() {
        return isMCLoaded() && AutomobilityMidnightControls.IN_CONTROLLER_MODE.get();
    }

    public static void initMCHandler() {
        if (isMCLoaded()) AutomobilityMidnightControls.init();
    }

    public static boolean isMCLoaded() {
        return FabricLoader.getInstance().isModLoaded("midnightcontrols");
    }
}
