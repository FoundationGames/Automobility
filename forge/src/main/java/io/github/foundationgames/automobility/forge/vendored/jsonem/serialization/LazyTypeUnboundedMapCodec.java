package io.github.foundationgames.automobility.forge.vendored.jsonem.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.BaseMapCodec;

import java.util.Map;
import java.util.function.Supplier;

public class LazyTypeUnboundedMapCodec<K, V> implements BaseMapCodec<K, V>, Codec<Map<K, V>> {
    private final Codec<K> keyCodec;
    private final Supplier<Codec<V>> elementCodecProvider;

    private Codec<V> elementCodec;

    public LazyTypeUnboundedMapCodec(Codec<K> keyCodec, Supplier<Codec<V>> elementCodecProvider) {
        this.keyCodec = keyCodec;
        this.elementCodecProvider = elementCodecProvider;
    }

    public static <K, V> LazyTypeUnboundedMapCodec<K, V> of(Codec<K> keyCodec, Supplier<Codec<V>> elementCodecProvider) {
        return new LazyTypeUnboundedMapCodec<>(keyCodec, elementCodecProvider);
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, input));
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public Codec<K> keyCodec() {
        return keyCodec;
    }

    @Override
    public Codec<V> elementCodec() {
        if (elementCodec == null) {
            elementCodec = elementCodecProvider.get();
        }

        return elementCodec;
    }
}