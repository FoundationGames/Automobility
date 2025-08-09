package io.github.foundationgames.automobility.sound;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.lwjgl.openal.AL10;

import java.util.function.IntConsumer;

public abstract class AutomobileSoundInstance extends AbstractTickableSoundInstance implements AdvancedSoundInstance {
    protected final Minecraft client;
    protected final AutomobileEntity automobile;

    private double lastDistance;

    private int fade = 0;
    private boolean die = false;

    public AutomobileSoundInstance(SoundEvent sound, Minecraft client, AutomobileEntity automobile) {
        super(sound, SoundSource.AMBIENT, automobile.getCommandSenderWorld().getRandom());
        this.client = client;
        this.automobile = automobile;
        this.looping = true;
        this.delay = 0;

        this.lastDistance = getPosition(automobile).subtract(client.player.position()).length();
    }

    protected abstract boolean canPlay(AutomobileEntity automobile);

    protected abstract float getPitch(AutomobileEntity automobile);

    protected abstract float getVolume(AutomobileEntity automobile);

    protected double dopplerScale() {
        return 0.16;
    }

    protected float audibleDistance() {
        return 18;
    }

    protected Vec3 getPosition(AutomobileEntity auto) {
        return auto.position();
    }

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

        var pos = getPosition(this.automobile);
        this.x = pos.x();
        this.y = pos.y();
        this.z = pos.z();

        this.pitch = this.getPitch(this.automobile);

        if (player.getVehicle() != this.automobile) {
            double distance = pos.subtract(player.position()).length();
            double dDist = Math.clamp(this.lastDistance - distance, -1.5, 1.5);
            this.pitch += (float) (dopplerScale() * dDist);

            this.lastDistance -= dDist;
        } else {
            this.lastDistance = 0;
        }
    }

    @Override
    public IntConsumer setupALState() {
        float dist = audibleDistance();
        return source -> AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, dist);
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
            return 0.7f;
        }

        @Override
        protected Vec3 getPosition(AutomobileEntity auto) {
            var ePos = auto.getFrame().model().enginePos().scale(1d/16);
            var ePosD = new Vector3d(ePos.x(), ePos.y(), ePos.z());
            auto.localPosToWorldSpace(ePosD);
            return new Vec3(ePosD.x(), ePosD.y(), ePosD.z());
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
            float volumeBoost = ((Math.clamp(automobile.getHSpeed(), 0.2f, 1.4f) - 0.2f) / 1.2f) * 3;
            return automobile.automobileOnGround() ? 0.75f + volumeBoost : 0;
        }

        @Override
        protected Vec3 getPosition(AutomobileEntity auto) {
            return auto.getTailPos();
        }
    }
}
