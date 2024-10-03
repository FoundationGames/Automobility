package io.github.foundationgames.automobility.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class DriftSmokeParticle extends SpriteBillboardParticle {
    private static final float SCALE_FACTOR = 0.83f;
    private static final int MAX_AGE = (int)(Math.log(0.1) / Math.log(SCALE_FACTOR));

    protected DriftSmokeParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        this.scale = 0.6f - (world.random.nextFloat() * 0.1f);
        this.alpha = this.scale;
        this.setMaxAge(MAX_AGE);
    }

    @Override
    public void tick() {
        this.scale(SCALE_FACTOR);
        this.alpha = this.scale;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
            var particle = new DriftSmokeParticle(world, x, y, z);
            particle.setSprite(this.sprites);
            return particle;
        }
    }
}
