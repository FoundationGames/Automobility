package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.item.SlopePlacementContext;
import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SlopeBlock extends HorizontalFacingBlock implements Waterloggable {
    public static final VoxelShape NORTH_BOTTOM_SHAPE;
    public static final VoxelShape SOUTH_BOTTOM_SHAPE;
    public static final VoxelShape EAST_BOTTOM_SHAPE;
    public static final VoxelShape WEST_BOTTOM_SHAPE;
    public static final VoxelShape NORTH_TOP_SHAPE;
    public static final VoxelShape SOUTH_TOP_SHAPE;
    public static final VoxelShape EAST_TOP_SHAPE;
    public static final VoxelShape WEST_TOP_SHAPE;

    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public SlopeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(HALF, BlockHalf.BOTTOM).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return ctx instanceof SlopePlacementContext slopeCtx ?
                super.getPlacementState(ctx)
                        .with(FACING, slopeCtx.getSlopeFacing())
                        .with(WATERLOGGED, ctx.getWorld().getBlockState(ctx.getBlockPos()).isOf(Blocks.WATER))
                        .with(HALF, slopeCtx.getSlopeHalf())
                :
                super.getPlacementState(ctx)
                        .with(FACING, ctx.getPlayerFacing().getOpposite())
                        .with(WATERLOGGED, ctx.getWorld().getBlockState(ctx.getBlockPos()).isOf(Blocks.WATER))
                        .with(HALF, ctx.getHitPos().y - ctx.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, HALF, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(HALF)) {
            case BOTTOM -> switch (state.get(FACING)) {
                case NORTH -> NORTH_BOTTOM_SHAPE;
                case SOUTH -> SOUTH_BOTTOM_SHAPE;
                case WEST -> WEST_BOTTOM_SHAPE;
                case EAST -> EAST_BOTTOM_SHAPE;
                default -> VoxelShapes.empty();
            };
            case TOP -> switch (state.get(FACING)) {
                case NORTH -> NORTH_TOP_SHAPE;
                case SOUTH -> SOUTH_TOP_SHAPE;
                case WEST -> WEST_TOP_SHAPE;
                case EAST -> EAST_TOP_SHAPE;
                default -> VoxelShapes.empty();
            };
            default -> VoxelShapes.empty();
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
                var shape = switch (dir) {
                    case NORTH -> Block.createCuboidShape(0, -1.5, 0, 16, 0.5, 0.5);
                    case SOUTH -> Block.createCuboidShape(0, -1.5, 15.5, 16, 0.5, 16);
                    case EAST -> Block.createCuboidShape(15.5, -1.5, 0, 16, 0.5, 16);
                    case WEST -> Block.createCuboidShape(0, -1.5, 0, 0.5, 0.5, 16);
                    default -> VoxelShapes.empty();
                };
                shape = shape.offset(0, i * 0.5, 0);
                var finalShape = shape;
                for (int j = 1; j < 32; j++) {
                    finalShape = VoxelShapes.union(finalShape, shape.offset((ox * j) / 16, (0.25 * j) / 16, (oz * j) / 16));
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
    }
}
