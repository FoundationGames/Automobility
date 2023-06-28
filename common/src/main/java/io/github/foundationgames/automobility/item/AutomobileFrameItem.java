package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileFrame;

public class AutomobileFrameItem extends AutomobileComponentItem<AutomobileFrame> {
    public AutomobileFrameItem(Properties settings) {
        super(settings, "frame", "frame", AutomobileFrame.REGISTRY);
    }
}
