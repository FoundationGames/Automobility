package io.github.foundationgames.automobility.forge;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.forge.network.AutomobilityPacketHandler;
import io.github.foundationgames.automobility.util.InitlessConstants;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod(InitlessConstants.AUTOMOBILITY)
@Mod.EventBusSubscriber(modid = InitlessConstants.AUTOMOBILITY, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AutomobilityForge {
    public AutomobilityForge() {
        ForgePlatform.init();

        Automobility.init();
        AutomobilityPacketHandler.init();
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void registerAll(RegisterEvent evt) {
        register(Registry.BLOCK, evt);
        register(Registry.BLOCK_ENTITY_TYPE, evt);
        register(Registry.ITEM, evt);
        register(Registry.ENTITY_TYPE, evt);
        register(Registry.PARTICLE_TYPE, evt);
        register(Registry.SOUND_EVENT, evt);
        register(Registry.MENU, evt);
        register(Registry.RECIPE_TYPE, evt);
        register(Registry.RECIPE_SERIALIZER, evt);
    }

    public static <T> void register(Registry<T> registry, RegisterEvent evt) {
        if (registry == evt.getVanillaRegistry()) {

            if (evt.getForgeRegistry() != null) {
                evt.register(evt.<T>getForgeRegistry().getRegistryKey(), helper -> {
                    for (var entry : RegistryQueue.getQueue(registry)) {
                        helper.register(entry.rl(), entry.entry().create());
                    }
                });
            } else {
                for (var entry : RegistryQueue.getQueue(registry)) {
                    evt.register(registry.key(), entry.rl(), entry.entry()::create);
                }
            }
        }
    }
}
