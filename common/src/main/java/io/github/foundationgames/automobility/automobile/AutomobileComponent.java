package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;

public interface AutomobileComponent<T extends AutomobileComponent<T>> extends SimpleMapContentRegistry.Identifiable, StatContainer<T> {
    boolean isEmpty();
}
