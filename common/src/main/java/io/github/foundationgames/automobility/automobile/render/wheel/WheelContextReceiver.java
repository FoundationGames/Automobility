package io.github.foundationgames.automobility.automobile.render.wheel;

import io.github.foundationgames.automobility.automobile.WheelBase;

public interface WheelContextReceiver {
    void provideContext(WheelBase.WheelPos pos);
}
