package io.github.foundationgames.automobility.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class OffRoadBlock extends Block {
    public static final VoxelShape ONE_LAYER_SHAPE = createCuboidShape(0, 0, 0, 16, 2, 16);
    public static final VoxelShape TWO_LAYER_SHAPE = createCuboidShape(0, 0, 0, 16, 4, 16);
    public static final VoxelShape THREE_LAYER_SHAPE = createCuboidShape(0, 0, 0, 16, 6, 16);

    public static final IntProperty LAYERS = IntProperty.of("layers", 1, 3);

    public final Vector3f color;

    public OffRoadBlock(Settings settings, Vector3f color) {
        super(settings);
        setDefaultState(getDefaultState().with(LAYERS, 1));
        this.color = color;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        var state = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (state.isOf(this) && state.get(LAYERS) < 3) {
            return state.with(LAYERS, state.get(LAYERS) + 1);
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return state.get(LAYERS) < 3;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (!canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), Direction.UP);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(LAYERS)) {
            case 2 -> TWO_LAYER_SHAPE;
            case 3 -> THREE_LAYER_SHAPE;
            default -> ONE_LAYER_SHAPE;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LAYERS);
    }
}
