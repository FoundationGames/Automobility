package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RearAttachmentItem extends AutomobileComponentItem<RearAttachmentType<?>> implements AutomobileInteractable {
    public RearAttachmentItem(Properties settings) {
        super(settings, "attachment", "attachment.rear", RearAttachmentType.REGISTRY);
    }

    @Override
    public InteractionResult interactAutomobile(ItemStack stack, Player player, InteractionHand hand, AutomobileEntity automobile) {
        if (automobile.getRearAttachment().type.isEmpty()) {
            if (player.level().isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            automobile.setRearAttachment(getComponent(stack));
            automobile.playHitSound(automobile.getTailPos());
            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        return InteractionResult.PASS;
    }
}
