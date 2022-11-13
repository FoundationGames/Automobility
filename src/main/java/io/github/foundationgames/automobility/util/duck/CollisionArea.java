package io.github.foundationgames.automobility.util.duck;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

public interface CollisionArea {
    boolean isPointInside(double x, double y, double z);

    boolean boxIntersects(Box box);

    double highestY(double x, double y, double z);

    static CollisionArea box(double ax, double ay, double az, double bx, double by, double bz) {
        return (CollisionArea) new Box(ax, ay, az, bx, by, bz);
    }

    static CollisionArea entity(Entity entity) {
        if (entity instanceof CollisionArea col) {
            return col;
        }

        return (CollisionArea) entity.getBoundingBox();
    }
}
