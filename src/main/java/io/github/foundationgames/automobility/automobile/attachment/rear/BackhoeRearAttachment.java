package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class BackhoeRearAttachment extends BasePlowRearAttachment {
    public static final List<Block> TILLABLE_BLOCKS = List.of(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.DIRT_PATH);

    public BackhoeRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    @Override
    public SoundEvent plowSound() {
        return SoundEvents.ITEM_HOE_TILL;
    }

    @Override
    public double searchHeight() {
        return -0.25;
    }

    @Override
    public BlockState plowResult(BlockPos pos, BlockState state) {
        if (!this.world().getBlockState(pos.up()).isAir()) {
            return state;
        }

        return TILLABLE_BLOCKS.contains(state.getBlock()) ? Blocks.FARMLAND.getDefaultState() : state;
    }
}
