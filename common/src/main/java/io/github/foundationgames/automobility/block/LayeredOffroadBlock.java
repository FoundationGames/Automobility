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

public class LayeredOffroadBlock extends Block implements OffroadBlock {
    public static final int MAX_LAYERS = 8;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, MAX_LAYERS);

    public final Vector3f color;
    private static final VoxelShape[] SHAPES_BY_LAYER;

    static {
        SHAPES_BY_LAYER = new VoxelShape[MAX_LAYERS];
        for (int i = 0; i < SHAPES_BY_LAYER.length; i++) {
            SHAPES_BY_LAYER[i] = getShapeForLayerCount(i + 1);
        }
    }

    public LayeredOffroadBlock(Properties settings, Vector3f color) {
        super(settings.pushReaction(PushReaction.DESTROY));
        registerDefaultState(defaultBlockState().setValue(LAYERS, 1));
        this.color = color;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (state.is(this) && state.getValue(LAYERS) < MAX_LAYERS) {
            return state.setValue(LAYERS, state.getValue(LAYERS) + 1);
        }
        return super.getStateForPlacement(ctx);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return state.getValue(LAYERS) < MAX_LAYERS && context.getItemInHand().is(this.asItem());
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
        int shapeIndex = state.getValue(LAYERS) - 1;
        if(shapeIndex >= 0 && shapeIndex < SHAPES_BY_LAYER.length) {
            return SHAPES_BY_LAYER[shapeIndex];
        } else {
            return SHAPES_BY_LAYER[0];
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LAYERS);
    }

    @Override
    public float getSpeedMultiplier(BlockState blockState) {
        int layers = blockState.getValue(LayeredOffroadBlock.LAYERS);
        return (float) ((-40 * layers) + 333) / ((50 * layers) + 365);
    }

    @Override
    public Vector3f getDebrisColor(BlockState blockState, BlockPos position, BlockGetter level) {
        return color;
    }

    private static VoxelShape getShapeForLayerCount(int layers) {
        return box(0, 0, 0, 16, 2 * layers, 16);
    }
}
