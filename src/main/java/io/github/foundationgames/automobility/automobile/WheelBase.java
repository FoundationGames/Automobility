package io.github.foundationgames.automobility.automobile;

public class WheelBase {
    public final WheelPos[] wheels;
    public final int wheelCount;

    public WheelBase(WheelPos ... wheels) {
        this.wheels = wheels;
        this.wheelCount = wheels.length;
    }

    public enum WheelSide {
        LEFT,
        RIGHT
    }

    public enum WheelEnd {
        FRONT,
        BACK
    }

    public static record WheelPos(float forward, float right, float scale, float yaw, WheelEnd end, WheelSide side) {}

    public static WheelBase basic(float separationLong, float separationWide) {
        return new BasicWheelBase(separationLong, separationWide);
    }
}
