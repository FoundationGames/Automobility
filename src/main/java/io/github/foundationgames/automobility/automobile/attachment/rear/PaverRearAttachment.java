package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.mixin.ShovelItemAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;

public class PaverRearAttachment extends BasePlowRearAttachment {
    public PaverRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    @Override
    public SoundEvent plowSound() {
        return SoundEvents.SHOVEL_FLATTEN;
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

        return ShovelItemAccess.getFlattenables().getOrDefault(state.getBlock(), state);
    }
}