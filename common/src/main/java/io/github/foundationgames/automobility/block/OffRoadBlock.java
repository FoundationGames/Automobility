package io.github.foundationgames.automobility.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class OffRoadBlock extends Block {
    public static final VoxelShape ONE_LAYER_SHAPE = box(0, 0, 0, 16, 2, 16);
    public static final VoxelShape TWO_LAYER_SHAPE = box(0, 0, 0, 16, 4, 16);
    public static final VoxelShape THREE_LAYER_SHAPE = box(0, 0, 0, 16, 6, 16);

    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 3);

    public final Vector3f color;

    public OffRoadBlock(Properties settings, Vector3f color) {
        super(settings.pushReaction(PushReaction.DESTROY));
        registerDefaultState(defaultBlockState().setValue(LAYERS, 1));
        this.color = color;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (state.is(this) && state.getValue(LAYERS) < 3) {
            return state.setValue(LAYERS, state.getValue(LAYERS) + 1);
        }
        return super.getStateForPlacement(ctx);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return state.getValue(LAYERS) < 3 && context.getItemInHand().is(this.asItem());
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, fromPos, notify);
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(LAYERS)) {
            case 2 -> TWO_LAYER_SHAPE;
            case 3 -> THREE_LAYER_SHAPE;
            default -> ONE_LAYER_SHAPE;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LAYERS);
    }
}
