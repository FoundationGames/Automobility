package io.github.foundationgames.automobility.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.Properties;

/**
 * Using switch-cases in mixins prevents you from hotswapping changes.
 * Yeah.
 */
public enum Methods {;
    public static float stairSlopeX(BlockState state) {
        if (state.get(Properties.BLOCK_HALF) == BlockHalf.TOP) return 0;
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case NORTH -> 45;
            case SOUTH -> -45;
            default -> 0;
        };
    }

    public static float stairSlopeZ(BlockState state) {
        if (state.get(Properties.BLOCK_HALF) == BlockHalf.TOP) return 0;
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case WEST -> -45;
            case EAST -> 45;
            default -> 0;
        };
    }
}
