package io.github.foundationgames.automobility.automobile;

public class BasicWheelBase extends WheelBase {
    public BasicWheelBase(float sepLong, float sepWide) {
        super(
                new WheelPos(sepLong / 2, sepWide / -2, 1, 0, WheelEnd.FRONT, WheelSide.LEFT),
                new WheelPos(sepLong / -2, sepWide / -2, 1, 0, WheelEnd.BACK, WheelSide.LEFT),
                new WheelPos(sepLong / 2, sepWide / 2, 1, 180, WheelEnd.FRONT, WheelSide.RIGHT),
                new WheelPos(sepLong / -2, sepWide / 2, 1, 180, WheelEnd.BACK, WheelSide.RIGHT)
        );
    }
}
