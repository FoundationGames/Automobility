package io.github.foundationgames.automobility.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public enum AUtils {;
    public static float zero(float in, float by) {
        if (Math.abs(in) < by) return 0;
        if (in > 0) by *= -1;
        in += by;
        return in;
    }

    public static NbtCompound v3dToNbt(Vec3d vec) {
        var r = new NbtCompound();
        r.putDouble("x", vec.getX());
        r.putDouble("y", vec.getY());
        r.putDouble("z", vec.getZ());
        return r;
    }

    public static Vec3d v3dFromNbt(NbtCompound nbt) {
        return new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
}
