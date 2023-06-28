package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.block.SteepSlopeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class SteepSlopeBlockItem extends BlockItem {
    public SteepSlopeBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Nullable
    @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        var hitPos = context.getClickLocation();
        var pos = new BlockPos((int) hitPos.x, (int) hitPos.y, (int) hitPos.z);
        var world = context.getLevel();
        if (world.getBlockState(pos).getBlock() instanceof SteepSlopeBlock) {
            var facing = world.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING);
            var playerFacing = context.getHorizontalDirection();
            var vOffset = playerFacing == facing ? Direction.DOWN : playerFacing == facing.getOpposite() ? Direction.UP : null;
            var place = pos.relative(playerFacing);
            if (vOffset != null) place = place.relative(vOffset);
            var pState = world.getBlockState(place);
            if (pState.isAir() || pState.is(Blocks.WATER)) {
                return new SlopePlacementContext(BlockPlaceContext.at(context, place, Direction.UP), facing);
            }
        }
        return super.updatePlacementContext(context);
    }
}
