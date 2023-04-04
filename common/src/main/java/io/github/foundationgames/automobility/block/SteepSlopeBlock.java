package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.item.SlopePlacementContext;
import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SteepSlopeBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final VoxelShape NORTH_SHAPE;
    public static final VoxelShape SOUTH_SHAPE;
    public static final VoxelShape EAST_SHAPE;
    public static final VoxelShape WEST_SHAPE;

    public static final VoxelShape OLD_NORTH_SHAPE;
    public static final VoxelShape OLD_SOUTH_SHAPE;
    public static final VoxelShape OLD_EAST_SHAPE;
    public static final VoxelShape OLD_WEST_SHAPE;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final boolean old;

    public SteepSlopeBlock(Properties settings, boolean old) {
        super(settings.noOcclusion());
        this.old = old;
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return ctx instanceof SlopePlacementContext slopeCtx ?
                super.getStateForPlacement(ctx)
                        .setValue(FACING, slopeCtx.getSlopeFacing())
                        .setValue(WATERLOGGED, ctx.getLevel().getBlockState(ctx.getClickedPos()).is(Blocks.WATER))
                :
                super.getStateForPlacement(ctx)
                        .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                        .setValue(WATERLOGGED, ctx.getLevel().getBlockState(ctx.getClickedPos()).is(Blocks.WATER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (old) {
            return switch (state.getValue(FACING)) {
                case NORTH -> OLD_NORTH_SHAPE;
                case SOUTH -> OLD_SOUTH_SHAPE;
                case WEST -> OLD_WEST_SHAPE;
                case EAST -> OLD_EAST_SHAPE;
                default -> Shapes.empty();
            };
        } else {
            return switch (state.getValue(FACING)) {
                case NORTH -> NORTH_SHAPE;
                case SOUTH -> SOUTH_SHAPE;
                case WEST -> WEST_SHAPE;
                case EAST -> EAST_SHAPE;
                default -> Shapes.empty();
            };
        }
    }

    static {
        var shapes = new ArrayList<VoxelShape>();
        for (var dir : AUtils.HORIZONTAL_DIRS) {
            double ox = switch (dir) {
                case WEST -> 0.5;
                case EAST -> -0.5;
                default -> 0;
            };
            double oz = switch (dir) {
                case NORTH -> 0.5;
                case SOUTH -> -0.5;
                default -> 0;
            };
            var finalShape = Shapes.empty();
            for (int j = 1; j < 32; j++) {
                finalShape = Shapes.or(finalShape, SlopeBlock.slopeStep(dir, (j * 0.5)).move((ox * j) / 16, 0, (oz * j) / 16));
            }
            shapes.add(finalShape);
        }
        NORTH_SHAPE = shapes.get(0);
        SOUTH_SHAPE = shapes.get(1);
        EAST_SHAPE = shapes.get(2);
        WEST_SHAPE = shapes.get(3);

        // OLD SLOPE SHAPES
        shapes.clear();
        for (var dir : AUtils.HORIZONTAL_DIRS) {
            double ox = switch (dir) {
                case WEST -> 0.5;
                case EAST -> -0.5;
                default -> 0;
            };
            double oz = switch (dir) {
                case NORTH -> 0.5;
                case SOUTH -> -0.5;
                default -> 0;
            };
            var shape = switch (dir) {
                case NORTH -> Block.box(0, -1.5, 0, 16, 0.5, 0.5);
                case SOUTH -> Block.box(0, -1.5, 15.5, 16, 0.5, 16);
                case EAST -> Block.box(15.5, -1.5, 0, 16, 0.5, 16);
                case WEST -> Block.box(0, -1.5, 0, 0.5, 0.5, 16);
                default -> Shapes.empty();
            };
            var finalShape = shape;
            for (int i = 1; i < 32; i++) {
                finalShape = Shapes.or(finalShape, shape.move((ox * i) / 16, (0.5 * i) / 16, (oz * i) / 16));
            }
            shapes.add(finalShape);
        }
        OLD_NORTH_SHAPE = shapes.get(0);
        OLD_SOUTH_SHAPE = shapes.get(1);
        OLD_EAST_SHAPE = shapes.get(2);
        OLD_WEST_SHAPE = shapes.get(3);
    }
}
