package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class SaddledBarrelRearAttachment extends ChestRearAttachment {
    public SaddledBarrelRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity, BlockState block, @Nullable BiFunction<ContainerLevelAccess, BlockRearAttachment, MenuProvider> screenProvider) {
        super(type, entity, block, screenProvider);
    }

    @Override
    protected SoundEvent getOpenSound() {
        return SoundEvents.BARREL_OPEN;
    }

    @Override
    protected SoundEvent getCloseSound() {
        return SoundEvents.BARREL_CLOSE;
    }

    @Override
    public Component getDisplayName() {
        return BaseChestRearAttachment.TITLE_BARREL;
    }

    @Override
    public boolean isRideable() {
        return true;
    }

    @Override
    public double getPassengerHeightOffset() {
        return 0.94;
    }
}
