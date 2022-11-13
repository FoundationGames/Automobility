package io.github.foundationgames.automobility.util;

@FunctionalInterface
public interface FloatFunc<V> {
    float apply(V val);
}
