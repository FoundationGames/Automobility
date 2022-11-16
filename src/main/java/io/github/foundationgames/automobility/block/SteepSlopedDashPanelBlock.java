package io.github.foundationgames.automobility.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SteepSlopedDashPanelBlock extends SteepSlopeBlock {
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    public SteepSlopedDashPanelBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(LEFT, false).setValue(RIGHT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LEFT, RIGHT);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);
        DashPanelBlock.onCollideWithDashPanel(entity);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        boolean left = world.getBlockState(pos.relative(state.getValue(FACING).getCounterClockWise(Direction.Axis.Y))).is(this);
        boolean right = world.getBlockState(pos.relative(state.getValue(FACING).getClockWise(Direction.Axis.Y))).is(this);
        return state.setValue(LEFT, left).setValue(RIGHT, right);
    }
}
