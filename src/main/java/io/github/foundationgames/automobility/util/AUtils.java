package io.github.foundationgames.automobility.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.util.Random;

public enum AUtils {;
    /**
     * A format for decimal values displaying the tenths
     * and hundredths place, along with the ones place and
     * optionally the tens, hundreds, and thousands places.
     */
    public static final DecimalFormat DEC_TWO_PLACES = new DecimalFormat("###0.00");

    private static final Random RANDOM = new Random();

    public static final Direction[] HORIZONTAL_DIRS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    /**
     * Shifts the number 'in' towards zero by the amount 'by'
     * @param in The number to shift
     * @param by The amount to shift it by
     * @return The result of the shift
     */
    public static float zero(float in, float by) {
        return shift(in, by, 0);
    }

    /**
     * Shifts the number 'in' towards the number 'to' by the amount 'by'
     * @param in The number to shift
     * @param by The amount to shift it by
     * @param to The number to shift it to
     * @return The result of the shift
     */
    public static float shift(float in, float by, float to) {
        if ((Math.abs(in - to)) < by) return to;
        if (in > to) by *= -1;
        in += by;
        return in;
    }

    /**
     * Returns whether the two passed values have the same sign (both negative/both positive)
     * @param a The first value
     * @param b The second value
     * @return Whether they have the same sign
     */
    public static boolean haveSameSign(float a, float b) {
        if (a == 0 || b == 0) {
            return a == b;
        }
        return a / Math.abs(a) == b / Math.abs(b);
    }

    /**
     * Puts a Vec3d to an NbtCompound
     * @param vec The vector to be written
     * @return An NbtCompound containing the values of the passed vector
     */
    public static NbtCompound v3dToNbt(Vec3d vec) {
        var r = new NbtCompound();
        r.putDouble("x", vec.getX());
        r.putDouble("y", vec.getY());
        r.putDouble("z", vec.getZ());
        return r;
    }

    /**
     * Gets a Vec3d from an NbtCompound created by AUtils.v3dToNbt()
     * @param nbt The NbtCompound to read from
     * @return A Vec3d containing the values in the passed NbtCompound
     */
    public static Vec3d v3dFromNbt(NbtCompound nbt) {
        return new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }

    /**
     * A shortcut method to render a Myron obj model, or any baked model without directional/blockstate-based quads
     * @param model The Myron BakedModel to render
     * @param vertices A vertex consumer buffer using a terrain render layer
     * @param matrices A matrix stack containing transformations
     * @param light The lightmap coordinates to render with
     * @param overlay The overlay coordinates to render with
     */
    public static void renderMyronObj(BakedModel model, VertexConsumer vertices, MatrixStack matrices, int light, int overlay) {
        for (BakedQuad quad : model.getQuads(null, null, RANDOM)) {
            vertices.quad(matrices.peek(), quad, 1, 1, 1, light, overlay);
        }
    }
}
