package io.github.foundationgames.automobility.util;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobilePrefab;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.text.DecimalFormat;
import java.util.Random;

public enum AUtils {;
    /**
     * A format for decimal values displaying the tenths
     * and hundredths place, along with the ones place and
     * optionally the tens, hundreds, and thousands places.
     */
    public static final DecimalFormat DEC_TWO_PLACES = new DecimalFormat("###0.00");

    /**
     * An array of all horizontal directions. Consists of
     * all directions except UP and DOWN.
     */
    public static final Direction[] HORIZONTAL_DIRS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    /**
     * Flag to allow entities to step up blocks without being on the ground
     * (necessary for automobiles, which update movement coarsely and might
     * confuse the server moving quickly through holes)
     */
    public static boolean IGNORE_ENTITY_GROUND_CHECK_STEPPING = false;

    private static final Random RANDOM = new Random();

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
     * Shifts the number 'in' towards zero by the amount 'by'
     * @param in The number to shift
     * @param by The amount to shift it by
     * @return The result of the shift
     */
    public static int zero(int in, int by) {
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
     * Shifts the number 'in' towards the number 'to' by the amount 'by'
     * @param in The number to shift
     * @param by The amount to shift it by
     * @param to The number to shift it to
     * @return The result of the shift
     */
    public static int shift(int in, int by, int to) {
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
     * Between the passed values a and b, returns the one which is furthest from zero.
     * @param a The first value
     * @param b The second value
     * @return The value which is furthest away from zero
     */
    public static float furthestFromZero(float a, float b) {
        return Math.abs(a) > Math.abs(b) ? a : Math.abs(b) > Math.abs(a) ? b : a;
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
        // For some reason with Iris, model.getQuads() throws a NPE
        try {
            for (BakedQuad quad : model.getQuads(null, null, RANDOM)) {
                vertices.quad(matrices.peek(), quad, 1, 1, 1, light, overlay);
            }
        } catch (NullPointerException ignored) {}
    }

    /**
     * Turns an RGB color integer into a Vec3f.
     * @param color An RGB color integer
     * @return A Vec3f containing the color integer's RGB, with x being r, y being g, and z being b. All values are from 0 to 1.
     */
    public static Vec3f colorFromInt(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return new Vec3f((float)r / 255, (float)g / 255, (float)b / 255);
    }

    public static boolean canMerge(ItemStack a, ItemStack b) {
        return (a.isItemEqual(b)) && (a.getCount() + b.getCount() <= a.getMaxCount());
    }

    /**
     * Tries to insert an ItemStack into an inventory.
     *
     * @param stack The item to insert
     * @param inv   The inventory to insert into
     * @return {@code true} if the entire stack was consumed, or {@code false} if some or all of the stack is remaining
     */
    public static boolean transferInto(ItemStack stack, Inventory inv) {
        for (int slot = 0; slot < inv.size(); slot++) {
            if (inv.isValid(slot, stack)) {
                var slotStack = inv.getStack(slot);
                if (slotStack.isEmpty()) {
                    inv.setStack(slot, stack);
                    return true;
                }
                if (canMerge(slotStack, stack)) {
                    int amount = Math.min(stack.getCount(), stack.getMaxCount() - slotStack.getCount());
                    stack.decrement(amount);
                    slotStack.increment(amount);
                    return stack.isEmpty();
                }
            }
        }
        return false;
    }

    // Placed here because class loading is jank

    public static ItemStack createGroupIcon() {
        return new ItemStack(AutomobilityItems.CROWBAR);
    }

    public static ItemStack createCourseElementsIcon() {
        return new ItemStack(AutomobilityBlocks.SLOPED_DASH_PANEL);
    }

    public static ItemStack createPrefabsIcon() {
        return new AutomobilePrefab(Automobility.id("standard_light_blue"), AutomobileFrame.STANDARD_LIGHT_BLUE, AutomobileWheel.STANDARD, AutomobileEngine.IRON).toStack();
    }
}
