package io.github.foundationgames.automobility.controller;

public interface AutomobileController {
    boolean accelerating();

    boolean braking();

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
        public boolean accelerating() {
            return false;
        }

        @Override
        public boolean braking() {
            return false;
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
