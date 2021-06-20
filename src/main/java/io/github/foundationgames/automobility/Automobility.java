package io.github.foundationgames.automobility;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Automobility implements ModInitializer {

    public static final String MOD_ID = "automobility";

    @Override
    public void onInitialize() {

    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
