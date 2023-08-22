package io.github.foundationgames.automobility.controller;

public interface AutomobileController {
    float acceleration();

    float brakeForce();

    boolean drifting();

    default void crashRumble() {}

    default void groundThudRumble() {}

    default void driftChargeRumble() {}

    default void updateMaxChargeRumbleState(boolean maxCharge) {}

    default void updateBoostingRumbleState(boolean boosting, float boostPower) {}

    default void updateOffRoadRumbleState(boolean inOffRoad) {}

    boolean inControllerMode();

    default void initCompat() {}

    AutomobileController INCOMPATIBLE = new AutomobileController() {
        @Override
        public float acceleration() {
            return 0f;
        }

        @Override
        public float brakeForce() {
            return 0f;
        }

        @Override
        public boolean drifting() {
            return false;
        }

        @Override
        public boolean inControllerMode() {
            return false;
        }

    };
}
