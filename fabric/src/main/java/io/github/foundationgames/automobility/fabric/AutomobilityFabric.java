package io.github.foundationgames.automobility.fabric;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.fabric.block.AutomobilityFabricBlocks;
import io.github.foundationgames.automobility.fabric.resource.AutomobilityData;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

public class AutomobilityFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricPlatform.init();
        Automobility.init();

        register(Registry.BLOCK);
        register(Registry.BLOCK_ENTITY_TYPE);
        register(Registry.ITEM);
        register(Registry.ENTITY_TYPE);
        register(Registry.PARTICLE_TYPE);
        register(Registry.SOUND_EVENT);
        register(Registry.MENU);
        register(Registry.RECIPE_TYPE);
        register(Registry.RECIPE_SERIALIZER);

        AutomobilityData.setup();
        AutomobilityFabricBlocks.init();
    }

    public static <V> void register(Registry<V> registry) {
        RegistryQueue.getQueue(registry).forEach(e -> Registry.register(registry, e.rl(), e.entry().create()));
    }
}
