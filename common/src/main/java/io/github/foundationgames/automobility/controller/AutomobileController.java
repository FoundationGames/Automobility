package io.github.foundationgames.automobility.controller;

public interface AutomobileController {
    boolean accelerating();

    boolean braking();

    boolean drifting();

    default void crashRumble() {}

    default void updateDriftRumbleState(boolean drifting) {};

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
