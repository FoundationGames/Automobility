package io.github.foundationgames.automobility.util.midnightcontrols;

import net.fabricmc.loader.api.FabricLoader;

public enum ControllerUtils {;
    public static boolean accelerating() {
        return isMidnightControlsLoaded() && AutomobilityMidnightControls.ACCELERATE.isButtonDown();
    }

    public static boolean braking() {
        return isMidnightControlsLoaded() && AutomobilityMidnightControls.BRAKE.isButtonDown();
    }

    public static boolean drifting() {
        return isMidnightControlsLoaded() && AutomobilityMidnightControls.DRIFT.isButtonDown();
    }

    public static boolean inControllerMode() {
        return isMidnightControlsLoaded() && AutomobilityMidnightControls.IN_CONTROLLER_MODE.get();
    }

    public static void initMidnightControlsHandler() {
        if (isMidnightControlsLoaded()) AutomobilityMidnightControls.init();
    }

    public static boolean isMidnightControlsLoaded() {
        return FabricLoader.getInstance().isModLoaded("midnightcontrols");
    }
}
