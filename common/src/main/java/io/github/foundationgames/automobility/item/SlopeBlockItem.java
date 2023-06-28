package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.block.SlopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.jetbrains.annotations.Nullable;

public class SlopeBlockItem extends BlockItem {
    public SlopeBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Nullable
    @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        var hitPos = context.getClickLocation();
        var pos = BlockPos.containing(hitPos);
        var world = context.getLevel();
        if (world.getBlockState(pos).getBlock() instanceof SlopeBlock) {
            var facing = world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING);
            var half = world.getBlockState(pos).getValue(BlockStateProperties.HALF);
            var playerFacing = context.getHorizontalDirection();
            var vOffset = playerFacing == facing && half == Half.BOTTOM ? Direction.DOWN : playerFacing == facing.getOpposite() && half == Half.TOP ? Direction.UP : null;
            var place = pos.relative(playerFacing);
            if (vOffset != null) place = place.relative(vOffset);
            var pState = world.getBlockState(place);
            var nHalf = half;
            if (playerFacing == facing || playerFacing == facing.getOpposite()) nHalf = half == Half.TOP ? Half.BOTTOM : Half.TOP;
            if (pState.isAir() || pState.is(Blocks.WATER)) {
                return new SlopePlacementContext(BlockPlaceContext.at(context, place, Direction.UP), facing, nHalf);
            }
        }
        return super.updatePlacementContext(context);
    }
}
