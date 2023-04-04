package io.github.foundationgames.automobility.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.Half;

public class SlopePlacementContext extends BlockPlaceContext {
    private final Direction slopeFacing;
    private final Half slopeHalf;

    public SlopePlacementContext(UseOnContext context, Direction slopeFacing, Half slopeHalf) {
        super(context);
        this.slopeFacing = slopeFacing;
        this.slopeHalf = slopeHalf;
    }

    public SlopePlacementContext(UseOnContext context, Direction slopeFacing) {
        this(context, slopeFacing, Half.BOTTOM);
    }

    public Direction getSlopeFacing() {
        return slopeFacing;
    }

    public Half getSlopeHalf() {
        return slopeHalf;
    }
}
