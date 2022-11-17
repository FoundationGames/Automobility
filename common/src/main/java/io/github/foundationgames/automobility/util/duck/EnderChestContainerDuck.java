package io.github.foundationgames.automobility.util.duck;

import io.github.foundationgames.automobility.automobile.attachment.rear.BaseChestRearAttachment;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

public interface EnderChestContainerDuck {
    void automobility$setActiveAttachment(BaseChestRearAttachment attachment);

    static EnderChestContainerDuck of(PlayerEnderChestContainer inv) {
        return (EnderChestContainerDuck) inv;
    }
}
