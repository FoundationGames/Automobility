package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SlopedDashPanelBlock extends SlopeBlock {
    public static final BooleanProperty LEFT = BooleanProperty.of("left");
    public static final BooleanProperty RIGHT = BooleanProperty.of("right");

    public SlopedDashPanelBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LEFT, false).with(RIGHT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LEFT, RIGHT);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (entity instanceof AutomobileEntity auto) {
            auto.boost(0.45f, 50);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        boolean left = world.getBlockState(pos.offset(state.get(FACING).rotateCounterclockwise(Direction.Axis.Y))).isOf(this);
        boolean right = world.getBlockState(pos.offset(state.get(FACING).rotateClockwise(Direction.Axis.Y))).isOf(this);
        return state.with(LEFT, left).with(RIGHT, right);
    }
}
