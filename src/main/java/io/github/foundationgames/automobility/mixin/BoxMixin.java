package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.util.duck.CollisionArea;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Box.class)
public class BoxMixin implements CollisionArea {
    @Override
    public boolean isPointInside(double x, double y, double z) {
        var self = (Box)(Object)this;
        return (x >= self.minX && x <= self.maxX) &&
                (y >= self.minY && y <= self.maxY) &&
                (z >= self.minZ && z <= self.maxZ);
    }

    @Override
    public boolean boxIntersects(Box box) {
        return ((Box)(Object)this).intersects(box);
    }

    @Override
    public double highestY(double x, double y, double z) {
        return ((Box)(Object)this).maxY;
    }
}
