package io.github.foundationgames.automobility.particle;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.Eventual;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class AutomobilityParticles {
    public static final Eventual<SimpleParticleType> DRIFT_SMOKE = RegistryQueue.register(BuiltInRegistries.PARTICLE_TYPE, Automobility.rl("drift_smoke"), () -> Platform.get().simpleParticleType(true));

    public static void init() {
    }
}
