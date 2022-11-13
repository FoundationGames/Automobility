package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class FrontAttachmentItem extends AutomobileComponentItem<FrontAttachmentType<?>> implements AutomobileInteractable {
    public FrontAttachmentItem(Settings settings) {
        super(settings, "attachment", "attachment.front", FrontAttachmentType.REGISTRY);
    }

    @Override
    public ActionResult interactAutomobile(ItemStack stack, PlayerEntity player, Hand hand, AutomobileEntity automobile) {
        if (automobile.getFrontAttachment().type.isEmpty()) {
            if (player.world.isClient()) {
                return ActionResult.SUCCESS;
            }

            automobile.setFrontAttachment(getComponent(stack));
            automobile.playHitSound(automobile.getHeadPos());
            if (!player.isCreative()) {
                stack.decrement(1);
            }
        }

        return ActionResult.PASS;
    }
}