package io.github.foundationgames.automobility.entity;

import net.minecraft.entity.player.PlayerEntity;

public interface EntityWithInventory {
    boolean hasInventory();

    void openInventory(PlayerEntity player);
}
