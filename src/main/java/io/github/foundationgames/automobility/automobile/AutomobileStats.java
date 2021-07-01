package io.github.foundationgames.automobility.automobile;

public class AutomobileStats {
    private float acceleration = 0;     // 0-1
    private float comfortableSpeed = 0; // Blocks per Tick
    private float handling = 0;         // 0-1
    private float grip = 0;             // 0-1

    public AutomobileStats() {
    }

    public void from(AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {
        this.acceleration = ((1 - ((frame.weight() + wheel.size()) / 2)) + (2 * engine.torque()) / 3);
        this.comfortableSpeed = ((engine.speed() * 3) + ((engine.speed() * frame.weight()) * 2) + (engine.speed() * wheel.size())) / 5.7f;
        this.handling = ((1 - wheel.size()) + (1 - frame.weight()) + 2) / 4;
        this.grip = (wheel.grip() + frame.weight()) / 2;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getComfortableSpeed() {
        return comfortableSpeed;
    }

    public float getHandling() {
        return handling;
    }

    public float getGrip() {
        return grip;
    }
}
