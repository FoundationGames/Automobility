package io.github.foundationgames.automobility.item;

import net.minecraft.block.enums.BlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class SlopePlacementContext extends ItemPlacementContext {
    private final Direction slopeFacing;
    private final BlockHalf slopeHalf;

    public SlopePlacementContext(ItemUsageContext context, Direction slopeFacing, BlockHalf slopeHalf) {
        super(context);
        this.slopeFacing = slopeFacing;
        this.slopeHalf = slopeHalf;
    }

    public SlopePlacementContext(ItemUsageContext context, Direction slopeFacing) {
        this(context, slopeFacing, BlockHalf.BOTTOM);
    }

    public Direction getSlopeFacing() {
        return slopeFacing;
    }

    public BlockHalf getSlopeHalf() {
        return slopeHalf;
    }
}
