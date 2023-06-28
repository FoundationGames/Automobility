package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FrontAttachmentItem extends AutomobileComponentItem<FrontAttachmentType<?>> implements AutomobileInteractable {
    public FrontAttachmentItem(Properties settings) {
        super(settings, "attachment", "attachment.front", FrontAttachmentType.REGISTRY);
    }

    @Override
    public InteractionResult interactAutomobile(ItemStack stack, Player player, InteractionHand hand, AutomobileEntity automobile) {
        if (automobile.getFrontAttachment().type.isEmpty()) {
            if (player.level().isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            automobile.setFrontAttachment(getComponent(stack));
            automobile.playHitSound(automobile.getHeadPos());
            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        return InteractionResult.PASS;
    }
}
