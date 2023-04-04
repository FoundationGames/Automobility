package io.github.foundationgames.automobility.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryQueue<V> extends ArrayList<RegistryQueue.Entry<V>> {
    private static final Map<Registry<?>, RegistryQueue<?>> QUEUES = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <V, E extends V> Eventual<E> register(Registry<V> registry, ResourceLocation rl, Supplier<E> entry) {
        var result = new Eventual<>(entry);
        QUEUES.computeIfAbsent(registry, reg -> new RegistryQueue<V>()).add((Entry) new Entry<>(result, rl));
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <V> RegistryQueue<V> getQueue(Registry<V> registry) {
        return (RegistryQueue<V>) QUEUES.get(registry);
    }

    public record Entry<V> (Eventual<V> entry, ResourceLocation rl) {}
}
