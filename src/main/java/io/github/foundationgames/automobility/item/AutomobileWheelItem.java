package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileWheel;

public class AutomobileWheelItem extends AutomobileComponentItem<AutomobileWheel> {
    public AutomobileWheelItem(Settings settings) {
        super(settings, "wheel", "wheel", AutomobileWheel.REGISTRY);
    }

    @Override
    protected boolean addToCreative(AutomobileWheel component) {
        return super.addToCreative(component) && component != AutomobileWheel.CONVERTIBLE;
    }
}
