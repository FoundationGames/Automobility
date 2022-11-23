package io.github.foundationgames.automobility.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class SteepSlopeWithDashPanelBlock extends SteepSlopeBlock {
    public SteepSlopeWithDashPanelBlock(Properties settings) {
        super(settings, false);

        registerDefaultState(defaultBlockState().setValue(DashPanelBlock.POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DashPanelBlock.POWERED);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, fromPos, notify);

        boolean levelPwr = level.hasNeighborSignal(pos);
        boolean selfPwr = state.getValue(DashPanelBlock.POWERED);

        if (levelPwr != selfPwr) {
            level.setBlockAndUpdate(pos, state.setValue(DashPanelBlock.POWERED, levelPwr));
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(AutomobilityBlocks.DASH_PANEL.require());
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);
        DashPanelBlock.onCollideWithDashPanel(state, entity);
    }
}
