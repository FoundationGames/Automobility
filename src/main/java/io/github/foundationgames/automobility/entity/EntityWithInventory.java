package io.github.foundationgames.automobility.entity;

import net.minecraft.world.entity.player.Player;

public interface EntityWithInventory {
    boolean hasInventory();

    void openInventory(Player player);
}
