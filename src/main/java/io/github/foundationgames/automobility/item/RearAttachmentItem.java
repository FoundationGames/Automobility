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

public class RearAttachmentItem extends Item implements AutomobileInteractable {
    public RearAttachmentItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult interactAutomobile(ItemStack stack, PlayerEntity player, Hand hand, AutomobileEntity automobile) {
        if (automobile.getRearAttachment().type.isEmpty()) {
            if (player.world.isClient()) {
                return ActionResult.SUCCESS;
            }

            automobile.setRearAttachment(getAttachment(stack));
            if (!player.isCreative()) {
                stack.decrement(1);
            }
        }

        return ActionResult.PASS;
    }

    public ItemStack createStack(RearAttachmentType<?> attachment) {
        var stack = new ItemStack(this);
        stack.getOrCreateNbt().putString("attachment", attachment.id().toString());
        return stack;
    }

    public RearAttachmentType<?> getAttachment(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("attachment")) {
            return RearAttachmentType.REGISTRY.getOrDefault(Identifier.tryParse(stack.getNbt().getString("attachment")));
        }
        return RearAttachmentType.EMPTY;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var id = this.getAttachment(stack).getId();
        tooltip.add(new TranslatableText("attachment.rear."+id.getNamespace()+"."+id.getPath()).formatted(Formatting.AQUA));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            RearAttachmentType.REGISTRY.forEach(type -> {
                if (!type.isEmpty()) stacks.add(this.createStack(type));
            });
        }
    }
}
