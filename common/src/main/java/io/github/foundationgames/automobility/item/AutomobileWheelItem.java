package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileWheel;

public class AutomobileWheelItem extends AutomobileComponentItem<AutomobileWheel> {
    public AutomobileWheelItem(Properties settings) {
        super(settings, "wheel", "wheel", AutomobileWheel.REGISTRY);
    }
}
