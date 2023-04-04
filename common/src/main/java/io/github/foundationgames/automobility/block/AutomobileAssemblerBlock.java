package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.block.entity.AutomobileAssemblerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AutomobileAssemblerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final Component USE_CROWBAR_DIALOG = Component.translatable("dialog.automobility.use_crowbar").withStyle(ChatFormatting.GOLD);
    public static final Component INCOMPLETE_AUTOMOBILE_DIALOG = Component.translatable("dialog.automobility.incomplete_automobile").withStyle(ChatFormatting.RED);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape BASE = Shapes.or(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(5, 4, 5, 11, 12, 11));

    private static final VoxelShape NORTH_SOUTH = Shapes.or(BASE,
            Block.box(0, 8, 6, 16, 12, 10));
    private static final VoxelShape EAST_WEST = Shapes.or(BASE,
            Block.box(6, 8, 0, 10, 12, 16));

    public AutomobileAssemblerBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, fromPos, notify);

        boolean power = world.hasNeighborSignal(pos);
        if (power != state.getValue(POWERED)) {
            world.setBlockAndUpdate(pos, state.setValue(POWERED, power));
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide() && placer instanceof Player player) {
            player.displayClientMessage(USE_CROWBAR_DIALOG, true);
        }

        super.setPlacedBy(world, pos, state, placer, itemStack);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof AutomobileAssemblerBlockEntity assembler) {
            return assembler.interact(player, hand);
        }

        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.is(this) && world.getBlockEntity(pos) instanceof AutomobileAssemblerBlockEntity assembler) {
            assembler.dropParts();
        }

        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST, WEST -> EAST_WEST;
            default -> NORTH_SOUTH;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutomobileAssemblerBlockEntity(pos, state);
    }
}
