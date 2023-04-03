package io.github.foundationgames.automobility.sound;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.registry.Registry;

public class AutomobilitySounds {
    public static final SoundEvent COLLISION = register("entity.automobile.collision");
    public static final SoundEvent LANDING = register("entity.automobile.landing");
    public static final SoundEvent SKID = register("entity.automobile.skid");

    public static final SoundEvent STONE_ENGINE = register("entity.automobile.stone_engine");
    public static final SoundEvent COPPER_ENGINE = register("entity.automobile.copper_engine");
    public static final SoundEvent IRON_ENGINE = register("entity.automobile.iron_engine");
    public static final SoundEvent GOLD_ENGINE = register("entity.automobile.gold_engine");
    public static final SoundEvent DIAMOND_ENGINE = register("entity.automobile.diamond_engine");
    public static final SoundEvent CREATIVE_ENGINE = register("entity.automobile.creative_engine");

    private static SoundEvent register(String path) {
        var id = Automobility.id(path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void init() {
    }
}
