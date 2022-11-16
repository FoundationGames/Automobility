package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileEngine;

public class AutomobileEngineItem extends AutomobileComponentItem<AutomobileEngine> {
    public AutomobileEngineItem(Properties settings) {
        super(settings, "engine", "engine", AutomobileEngine.REGISTRY);
    }
}
