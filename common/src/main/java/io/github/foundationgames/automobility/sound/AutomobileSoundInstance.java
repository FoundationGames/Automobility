package io.github.foundationgames.automobility.sound;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class AutomobileSoundInstance extends AbstractTickableSoundInstance {
    private final Minecraft client;
    private final AutomobileEntity automobile;

    private double lastDistance;

    private int fade = 0;
    private boolean die = false;

    public AutomobileSoundInstance(SoundEvent sound, Minecraft client, AutomobileEntity automobile) {
        super(sound, SoundSource.AMBIENT, automobile.getCommandSenderWorld().getRandom());
        this.client = client;
        this.automobile = automobile;
        this.looping = true;
        this.delay = 0;
    }

    protected abstract boolean canPlay(AutomobileEntity automobile);

    protected abstract float getPitch(AutomobileEntity automobile);

    protected abstract float getVolume(AutomobileEntity automobile);

    @Override
    public void tick() {
        var player = this.client.player;
        if (automobile.isRemoved() || player == null) {
            this.stop();
            return;
        } else if (!this.canPlay(automobile)) {
            this.die = true;
        }

        if (this.die) {
            if (this.fade > 0) this.fade--;
            else if (this.fade == 0) {
                this.stop();
                return;
            }
        } else if (this.fade < 3) {
            this.fade++;
        }
        this.volume = this.getVolume(this.automobile) * (float)fade / 3;

        this.x = this.automobile.getX();
        this.y = this.automobile.getY();
        this.z = this.automobile.getZ();

        this.pitch = this.getPitch(this.automobile);

        if (player.getVehicle() != this.automobile) {
            double distance = this.automobile.position().subtract(player.position()).length();
            this.pitch += (0.36 * Math.atan(lastDistance - distance));

            this.lastDistance = distance;
        } else {
            this.lastDistance = 0;
        }
    }

    public static class EngineSound extends AutomobileSoundInstance {
        public EngineSound(Minecraft client, AutomobileEntity automobile) {
            super(automobile.getEngine().sound().get(), client, automobile);
        }

        @Override
        protected boolean canPlay(AutomobileEntity automobile) {
            return automobile.engineRunning();
        }

        @Override
        protected float getPitch(AutomobileEntity automobile) {
            return (float) (Math.pow(4, (automobile.getEffectiveSpeed() - 0.9)) + 0.32);
        }

        @Override
        protected float getVolume(AutomobileEntity automobile) {
            return 1;
        }
    }

    public static class SkiddingSound extends AutomobileSoundInstance {
        public SkiddingSound(Minecraft client, AutomobileEntity automobile) {
            super(AutomobilitySounds.SKID.require(), client, automobile);
        }

        @Override
        protected boolean canPlay(AutomobileEntity automobile) {
            return automobile.isDrifting() || automobile.burningOut();
        }

        @Override
        protected float getPitch(AutomobileEntity automobile) {
            return automobile.burningOut() ? 0.75f :
                    1 + 0.056f * ((float)Math.min(automobile.getTurboCharge(), AutomobileEntity.LARGE_TURBO_TIME) / AutomobileEntity.LARGE_TURBO_TIME);
        }

        @Override
        protected float getVolume(AutomobileEntity automobile) {
            return automobile.automobileOnGround() ? 1 : 0;
        }
    }
}
