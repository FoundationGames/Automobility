package io.github.foundationgames.automobility.particle;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.intermediary.Intermediary;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;

public class AutomobilityParticles {
    public static final SimpleParticleType DRIFT_SMOKE = RegistryQueue.register(Registry.PARTICLE_TYPE, Automobility.rl("drift_smoke"), Intermediary.get().simpleParticleType(true));

    public static void init() {
    }

    public static void initClient() {
        Intermediary.get().particleFactory(DRIFT_SMOKE, DriftSmokeParticle.Factory::new);
    }
}
