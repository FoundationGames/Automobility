package io.github.foundationgames.automobility.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleMapContentRegistry<V extends SimpleMapContentRegistry.Identifiable> {
    private final Map<Identifier, V> entries = new Object2ObjectOpenHashMap<>();
    private final List<Identifier> orderedKeys = new ArrayList<>();

    public SimpleMapContentRegistry() {
    }

    public V register(V entry) {
        entries.put(entry.getId(), entry);
        orderedKeys.add(entry.getId());
        return entry;
    }

    public V get(Identifier name) {
        return entries.get(name);
    }

    public V getOrDefault(Identifier name) {
        if (orderedKeys.size() <= 0) throw new IllegalStateException("Tried to get from empty registry!");
        return entries.getOrDefault(name, entries.get(orderedKeys.get(0)));
    }

    public void iterateEntries(Consumer<V> action) {
        orderedKeys.forEach(k -> action.accept(entries.get(k)));
    }

    public interface Identifiable {
        Identifier getId();
    }
}
