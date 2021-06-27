package io.github.foundationgames.automobility.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SlopedBlock {
    float getGroundSlopeX(World world, BlockState state, BlockPos pos);

    float getGroundSlopeZ(World world, BlockState state, BlockPos pos);
}
