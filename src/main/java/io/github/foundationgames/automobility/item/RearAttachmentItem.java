package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RearAttachmentItem extends AutomobileComponentItem<RearAttachmentType<?>> implements AutomobileInteractable {
    public RearAttachmentItem(Settings settings) {
        super(settings, "attachment", "attachment.rear", RearAttachmentType.REGISTRY);
    }

    @Override
    public ActionResult interactAutomobile(ItemStack stack, PlayerEntity player, Hand hand, AutomobileEntity automobile) {
        if (automobile.getRearAttachment().type.isEmpty()) {
            if (player.world.isClient()) {
                return ActionResult.SUCCESS;
            }

            automobile.setRearAttachment(getComponent(stack));
            automobile.playHitSound();
            if (!player.isCreative()) {
                stack.decrement(1);
            }
        }

        return ActionResult.PASS;
    }
}
