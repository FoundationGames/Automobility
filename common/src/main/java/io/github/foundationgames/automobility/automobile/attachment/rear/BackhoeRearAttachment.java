package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BackhoeRearAttachment extends BasePlowRearAttachment {
    public static final List<Block> TILLABLE_BLOCKS = List.of(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.DIRT_PATH);

    public BackhoeRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    @Override
    public SoundEvent plowSound() {
        return SoundEvents.HOE_TILL;
    }

    @Override
    public double searchHeight() {
        return -0.25;
    }

    @Override
    public BlockState plowResult(BlockPos pos, BlockState state) {
        if (!this.world().getBlockState(pos.above()).isAir()) {
            return state;
        }

        return TILLABLE_BLOCKS.contains(state.getBlock()) ? Blocks.FARMLAND.defaultBlockState() : state;
    }
}
