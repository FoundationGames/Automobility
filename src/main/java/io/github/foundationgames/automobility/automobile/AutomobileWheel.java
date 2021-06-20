package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;

public record AutomobileWheel(
        float radiusPx,
        float widthPx,
        Ability ... abilities
) {
    public static final SimpleMapContentRegistry<AutomobileWheel> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileWheel STANDARD = REGISTRY.register(
            Automobility.id("standard"),
            new AutomobileWheel(3, 3)
    );

    public static final AutomobileWheel OFF_ROAD = REGISTRY.register(
            Automobility.id("off_road"),
            new AutomobileWheel(8, 4)
    );

    public static final AutomobileWheel STEEL = REGISTRY.register(
            Automobility.id("steel"),
            new AutomobileWheel(1.5f, 2)
    );

    public static final AutomobileWheel INFLATABLE = REGISTRY.register(
            Automobility.id("inflatable"),
            new AutomobileWheel(4f, 3, Ability.HYDROPLANE)
    );

    public enum Ability {
        HYDROPLANE;
    }
}
