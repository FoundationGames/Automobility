package io.github.foundationgames.automobility.util;

import java.util.Optional;
import java.util.function.Supplier;

public class Eventual<V> {
    private final Supplier<V> provider;
    private V value = null;

    public Eventual(Supplier<V> provider) {
        this.provider = provider;
    }

    public Optional<V> get() {
        return Optional.ofNullable(value);
    }

    public V require(String error) {
        if (this.value == null) {
            throw new RuntimeException(error);
        }

        return this.value;
    }

    public V require() {
        return this.require("Not yet created!");
    }

    public V create() {
        this.value = provider.get();
        return this.value;
    }
}
