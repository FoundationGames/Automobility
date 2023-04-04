package io.github.foundationgames.automobility.forge.vendored.jsonem.util;

import net.minecraft.client.model.geom.builders.UVPair;

/**
 * Implementation of {@code equals()} on {@link UVPair}
 */
public class UVPairComparable extends UVPair {
    public UVPairComparable(float x, float y) {
        super(x, y);
    }

    public static UVPairComparable of(UVPair vec) {
        return new UVPairComparable(vec.u(), vec.v());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UVPair vec) {
            return this.u() == vec.u() && this.v() == vec.v();
        }
        return super.equals(obj);
    }
}
