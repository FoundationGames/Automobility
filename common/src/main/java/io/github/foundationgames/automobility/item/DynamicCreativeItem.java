package io.github.foundationgames.automobility.item;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface DynamicCreativeItem {
	void fillItemCategory(List<ItemStack> stacks);
}
