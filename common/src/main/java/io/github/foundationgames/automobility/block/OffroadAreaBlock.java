package io.github.foundationgames.automobility.block;

import com.mojang.serialization.MapCodec;
import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class OffroadAreaBlock extends Block implements SimpleWaterloggedBlock, OffroadBlock {
    public static final int MAX_STRENGTH = 8;
    public static final MapCodec<OffroadAreaBlock> CODEC = Block.simpleCodec(OffroadAreaBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty STRENGTH = IntegerProperty.create("strength", 1, MAX_STRENGTH);
    public MapCodec<OffroadAreaBlock> codec() {
        return CODEC;
    }

    public OffroadAreaBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(STRENGTH, 2));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
        builder.add(STRENGTH);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player.canUseGameMasterBlocks()) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return context.isHoldingItem(asItem()) ? Shapes.block() : Shapes.empty();
    }

    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return setStrengthOnStack(super.getCloneItemStack(level, pos, state), (Integer)state.getValue(STRENGTH));
    }

    public static ItemStack setStrengthOnStack(ItemStack stack, int strength) {
        stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(STRENGTH, strength));
        return stack;
    }

    @Override
    public float getSpeedMultiplier(BlockState blockState) {
        int strength = blockState.getValue(OffroadAreaBlock.STRENGTH);
        return 1.0f / (strength + 0.5f);
    }

    @Override
    public Vector3f getDebrisColor(BlockState blockState, BlockPos position, BlockGetter level) {
        return AUtils.colorFromInt(level.getBlockState(position.below()).getMapColor(level, position).col);
    }
}