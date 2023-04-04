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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SlopeBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final VoxelShape NORTH_BOTTOM_SHAPE;
    public static final VoxelShape SOUTH_BOTTOM_SHAPE;
    public static final VoxelShape EAST_BOTTOM_SHAPE;
    public static final VoxelShape WEST_BOTTOM_SHAPE;
    public static final VoxelShape NORTH_TOP_SHAPE;
    public static final VoxelShape SOUTH_TOP_SHAPE;
    public static final VoxelShape EAST_TOP_SHAPE;
    public static final VoxelShape WEST_TOP_SHAPE;

    public static final VoxelShape OLD_NORTH_BOTTOM_SHAPE;
    public static final VoxelShape OLD_SOUTH_BOTTOM_SHAPE;
    public static final VoxelShape OLD_EAST_BOTTOM_SHAPE;
    public static final VoxelShape OLD_WEST_BOTTOM_SHAPE;
    public static final VoxelShape OLD_NORTH_TOP_SHAPE;
    public static final VoxelShape OLD_SOUTH_TOP_SHAPE;
    public static final VoxelShape OLD_EAST_TOP_SHAPE;
    public static final VoxelShape OLD_WEST_TOP_SHAPE;

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected final boolean old;

    public SlopeBlock(Properties settings, boolean old) {
        super(settings);
        this.old = old;
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return ctx instanceof SlopePlacementContext slopeCtx ?
                super.getStateForPlacement(ctx)
                        .setValue(FACING, slopeCtx.getSlopeFacing())
                        .setValue(WATERLOGGED, ctx.getLevel().getBlockState(ctx.getClickedPos()).is(Blocks.WATER))
                        .setValue(HALF, slopeCtx.getSlopeHalf())
                :
                super.getStateForPlacement(ctx)
                        .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                        .setValue(WATERLOGGED, ctx.getLevel().getBlockState(ctx.getClickedPos()).is(Blocks.WATER))
                        .setValue(HALF, ctx.getClickLocation().y - ctx.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, HALF, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (old) {
            return switch (state.getValue(HALF)) {
                case BOTTOM -> switch (state.getValue(FACING)) {
                    case NORTH -> OLD_NORTH_BOTTOM_SHAPE;
                    case SOUTH -> OLD_SOUTH_BOTTOM_SHAPE;
                    case WEST -> OLD_WEST_BOTTOM_SHAPE;
                    case EAST -> OLD_EAST_BOTTOM_SHAPE;
                    default -> Shapes.empty();
                };
                case TOP -> switch (state.getValue(FACING)) {
                    case NORTH -> OLD_NORTH_TOP_SHAPE;
                    case SOUTH -> OLD_SOUTH_TOP_SHAPE;
                    case WEST -> OLD_WEST_TOP_SHAPE;
                    case EAST -> OLD_EAST_TOP_SHAPE;
                    default -> Shapes.empty();
                };
                default -> Shapes.empty();
            };
        } else {
            return switch (state.getValue(HALF)) {
                case BOTTOM -> switch (state.getValue(FACING)) {
                    case NORTH -> NORTH_BOTTOM_SHAPE;
                    case SOUTH -> SOUTH_BOTTOM_SHAPE;
                    case WEST -> WEST_BOTTOM_SHAPE;
                    case EAST -> EAST_BOTTOM_SHAPE;
                    default -> Shapes.empty();
                };
                case TOP -> switch (state.getValue(FACING)) {
                    case NORTH -> NORTH_TOP_SHAPE;
                    case SOUTH -> SOUTH_TOP_SHAPE;
                    case WEST -> WEST_TOP_SHAPE;
                    case EAST -> EAST_TOP_SHAPE;
                    default -> Shapes.empty();
                };
                default -> Shapes.empty();
            };
        }
    }

    public static VoxelShape slopeStep(Direction dir, double height) {
        return switch (dir) {
            case NORTH -> Block.box(0, 0, 0, 16, height, 0.5);
            case SOUTH -> Block.box(0, 0, 15.5, 16, height, 16);
            case EAST -> Block.box(15.5, 0, 0, 16, height, 16);
            case WEST -> Block.box(0, 0, 0, 0.5, height, 16);
            default -> Shapes.empty();
        };
    }

    static {
        var shapes = new ArrayList<VoxelShape>();
        for (var dir : AUtils.HORIZONTAL_DIRS) {
            for (int i = 0; i < 2; i++) {
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
                for (int j = 0; j < 32; j++) {
                    finalShape = Shapes.or(finalShape, slopeStep(dir, (i * 8) + (j * 0.25)).move((ox * j) / 16, 0, (oz * j) / 16));
                }
                shapes.add(finalShape);
            }
        }
        NORTH_BOTTOM_SHAPE = shapes.get(0);
        NORTH_TOP_SHAPE = shapes.get(1);
        SOUTH_BOTTOM_SHAPE = shapes.get(2);
        SOUTH_TOP_SHAPE = shapes.get(3);
        EAST_BOTTOM_SHAPE = shapes.get(4);
        EAST_TOP_SHAPE = shapes.get(5);
        WEST_BOTTOM_SHAPE = shapes.get(6);
        WEST_TOP_SHAPE = shapes.get(7);

        // OLD SLOPE SHAPES
        shapes.clear();
        for (var dir : AUtils.HORIZONTAL_DIRS) {
            for (int i = 0; i < 2; i++) {
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
                shape = shape.move(0, i * 0.5, 0);
                var finalShape = shape;
                for (int j = 1; j < 32; j++) {
                    finalShape = Shapes.or(finalShape, shape.move((ox * j) / 16, (0.25 * j) / 16, (oz * j) / 16));
                }
                shapes.add(finalShape);
            }
        }
        OLD_NORTH_BOTTOM_SHAPE = shapes.get(0);
        OLD_NORTH_TOP_SHAPE = shapes.get(1);
        OLD_SOUTH_BOTTOM_SHAPE = shapes.get(2);
        OLD_SOUTH_TOP_SHAPE = shapes.get(3);
        OLD_EAST_BOTTOM_SHAPE = shapes.get(4);
        OLD_EAST_TOP_SHAPE = shapes.get(5);
        OLD_WEST_BOTTOM_SHAPE = shapes.get(6);
        OLD_WEST_TOP_SHAPE = shapes.get(7);
    }
}
