package io.github.foundationgames.automobility.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleMapContentRegistry<V> {
    private final Map<Identifier, V> entries = new Object2ObjectOpenHashMap<>();
    private final List<Identifier> orderedKeys = new ArrayList<>();

    public SimpleMapContentRegistry() {
    }

    public V register(Identifier name, V entry) {
        entries.put(name, entry);
        orderedKeys.add(name);
        return entry;
    }

    public V get(Identifier name) {
        return entries.get(name);
    }

    public void iterateEntries(Consumer<V> action) {
        orderedKeys.forEach(k -> action.accept(entries.get(k)));
    }
}
