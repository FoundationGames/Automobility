package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public interface AutomobileInteractable {
    ActionResult interactAutomobile(ItemStack stack, PlayerEntity player, Hand hand, AutomobileEntity automobile);
}
