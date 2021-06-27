package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.block.SteepSlopeBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class SteepSlopeBlockItem extends BlockItem {
    public SteepSlopeBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Nullable
    @Override
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        var hitPos = context.getHitPos();
        var pos = new BlockPos(Math.floor(hitPos.x), Math.floor(hitPos.y), Math.floor(hitPos.z));
        var world = context.getWorld();
        if (world.getBlockState(pos).getBlock() instanceof SteepSlopeBlock) {
            var facing = world.getBlockState(pos).get(Properties.HORIZONTAL_FACING);
            var playerFacing = context.getPlayerFacing();
            var vOffset = playerFacing == facing ? Direction.DOWN : playerFacing == facing.getOpposite() ? Direction.UP : null;
            var place = pos.offset(playerFacing);
            if (vOffset != null) place = place.offset(vOffset);
            if (world.getBlockState(place).isAir()) {
                //System.out.println(pos);
                return ItemPlacementContext.offset(context, place, Direction.UP);
            }
        }
        return super.getPlacementContext(context);
    }
}
