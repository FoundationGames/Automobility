package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class LaunchGelBlock extends Block {
    public static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 1, 16);

    public LaunchGelBlock(Settings settings) {
        super(settings);
    }

    public boolean canExistAt(WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos, Direction.UP);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (entity instanceof AutomobileEntity automobile && automobile.automobileOnGround()) {
            automobile.setSpeed(Math.max(automobile.getHSpeed(), 0.08f), automobile.getVSpeed() + 0.5f);
            automobile.boost(0.14f, 7);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return canExistAt(world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        if (!canExistAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
