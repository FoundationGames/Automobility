package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.mixin.ShovelItemAccess;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PaverRearAttachment extends BasePlowRearAttachment {
    public PaverRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    @Override
    public SoundEvent plowSound() {
        return SoundEvents.ITEM_SHOVEL_FLATTEN;
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

        return ShovelItemAccess.getPathStates().getOrDefault(state.getBlock(), state);
    }
}