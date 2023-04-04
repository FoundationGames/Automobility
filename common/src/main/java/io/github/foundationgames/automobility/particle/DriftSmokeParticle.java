package io.github.foundationgames.automobility.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class DriftSmokeParticle extends TextureSheetParticle {
    private static final float SCALE_FACTOR = 0.83f;
    private static final int MAX_AGE = (int)(Math.log(0.1) / Math.log(SCALE_FACTOR));

    protected DriftSmokeParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);
        this.quadSize = 0.6f - (world.random.nextFloat() * 0.1f);
        this.alpha = this.quadSize;
        this.setLifetime(MAX_AGE);
    }

    @Override
    public void tick() {
        this.scale(SCALE_FACTOR);
        this.alpha = this.quadSize;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            var particle = new DriftSmokeParticle(world, x, y, z);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
