package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.block.Sloped;
import io.github.foundationgames.automobility.util.Methods;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StairsBlock.class)
public class StairsBlockMixin implements Sloped {
    @Override
    public float getGroundSlopeX(World world, BlockState state, BlockPos pos) {
        return Methods.stairSlopeX(state);
    }

    @Override
    public float getGroundSlopeZ(World world, BlockState state, BlockPos pos) {
        return Methods.stairSlopeZ(state);
    }

    @Override
    public boolean isSticky() {
        return false;
    }
}
