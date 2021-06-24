package io.github.foundationgames.automobility.automobile;

// TODO
public class AutomobileStats {
    private float acceleration;     // 0-1
    private float comfortableSpeed; // Blocks per Tick
    private float handling;         // 0-1
    private float drift;            // 0-1
    private float turbo;            // 0-1

    public float getAcceleration() {
        return acceleration;
    }

    public float getComfortableSpeed() {
        return comfortableSpeed;
    }

    public float getHandling() {
        return handling;
    }

    public float getDrift() {
        return drift;
    }

    public float getTurbo() {
        return turbo;
    }
}
