package io.github.foundationgames.automobility.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleMapContentRegistry<V extends SimpleMapContentRegistry.Identifiable> {
    private final Map<ResourceLocation, V> entries = new Object2ObjectOpenHashMap<>();
    private final List<ResourceLocation> orderedKeys = new ArrayList<>();

    public SimpleMapContentRegistry() {
    }

    public V register(V entry) {
        entries.put(entry.getId(), entry);
        orderedKeys.add(entry.getId());
        return entry;
    }

    public V get(ResourceLocation name) {
        return entries.get(name);
    }

    public V getOrDefault(ResourceLocation name) {
        if (orderedKeys.size() <= 0) throw new IllegalStateException("Tried to get from empty registry!");
        return entries.getOrDefault(name, entries.get(orderedKeys.get(0)));
    }

    public void forEach(Consumer<V> action) {
        orderedKeys.forEach(k -> action.accept(entries.get(k)));
    }

    public interface Identifiable {
        ResourceLocation getId();
    }
}
