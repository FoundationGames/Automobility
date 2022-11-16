package io.github.foundationgames.automobility.particle;

import io.github.foundationgames.automobility.Automobility;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;

public class AutomobilityParticles {
    public static final SimpleParticleType DRIFT_SMOKE = Registry.register(Registry.PARTICLE_TYPE, Automobility.rl("drift_smoke"), FabricParticleTypes.simple(true));

    public static void init() {
    }

    public static void initClient() {
        ParticleFactoryRegistry.getInstance().register(DRIFT_SMOKE, DriftSmokeParticle.Factory::new);
    }
}
