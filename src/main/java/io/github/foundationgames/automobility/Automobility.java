package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Automobility implements ModInitializer {

    public static final String MOD_ID = "automobility";

    @Override
    public void onInitialize() {
        AutomobilityBlocks.init();
        AutomobilityItems.init();
        AutomobilityEntities.init();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
