package io.github.foundationgames.automobility.util.duck;

import io.github.foundationgames.automobility.automobile.attachment.rear.BaseChestRearAttachment;
import net.minecraft.inventory.EnderChestInventory;

public interface EnderChestInventoryDuck {
    void automobility$setActiveAttachment(BaseChestRearAttachment attachment);

    static EnderChestInventoryDuck of(EnderChestInventory inv) {
        return (EnderChestInventoryDuck) inv;
    }
}
