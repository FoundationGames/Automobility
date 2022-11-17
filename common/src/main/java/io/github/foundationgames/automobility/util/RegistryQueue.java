package io.github.foundationgames.automobility.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistryQueue<V> extends ArrayList<RegistryQueue.Entry<V>> {
    private static final Map<Registry<?>, RegistryQueue<?>> QUEUES = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <V, E extends V> E register(Registry<V> registry, ResourceLocation rl, E entry) {
        QUEUES.computeIfAbsent(registry, reg -> new RegistryQueue<V>()).add((Entry) new Entry<V>(entry, rl));
        return entry;
    }

    @SuppressWarnings("unchecked")
    public static <V> RegistryQueue<V> getQueue(Registry<V> registry) {
        return (RegistryQueue<V>) QUEUES.get(registry);
    }

    public record Entry<V> (V entry, ResourceLocation rl) {}
}
