package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;

public interface AutomobileComponent extends SimpleMapContentRegistry.Identifiable {
    boolean isEmpty();
}
