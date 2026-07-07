package io.github.foundationgames.automobility.sound;

import io.github.foundationgames.automobility.automobile.HornSoundDefinition;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.util.function.IntConsumer;

public abstract class SlicedLoopingAutomobileSoundInstance extends AutomobileSoundInstance {
    public static final float TICK_LENGTH = 1f / 20;

    public final float loopStart;
    public final float loopEnd;
    protected boolean continueSlicedLoop = false;

    public SlicedLoopingAutomobileSoundInstance(SoundEvent sound, Minecraft client, AutomobileEntity automobile, float loopStart, float loopEnd) {
        super(sound, client, automobile);
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        this.looping = false;
    }

    @Override
    public IntConsumer updateALState() {
        final float lStart = this.loopStart;
        final float lEnd = this.loopEnd;
        final boolean mayContinue = this.continueSlicedLoop;

        return source -> {
            int buffer = AL10.alGetSourcei(source, AL10.AL_BUFFER);

            int size = AL10.alGetBufferi(buffer, AL10.AL_SIZE);
            int channels = AL10.alGetBufferi(buffer, AL10.AL_CHANNELS);
            int bits = AL10.alGetBufferi(buffer, AL10.AL_BITS);
            int freq = AL10.alGetBufferi(buffer, AL10.AL_FREQUENCY);

            float duration = (float) (size * 8) / (channels * bits * freq);

            if (Float.isNaN(duration)) {
                return;
            }

            boolean mayLoop = lStart > 0 && lEnd > 0;

            float s = Math.max(0, lStart);
            float e = Math.min(duration, lEnd);

            if (mayLoop) {
                float os = AL10.alGetSourcef(source, AL11.AL_SEC_OFFSET);
                if (mayContinue) {
                    if (os > e - TICK_LENGTH) {
                        AL10.alSourcef(source, AL11.AL_SEC_OFFSET, s);
                    }
                } else {
                    if (os < s) {
                        AL10.alSourcef(source, AL11.AL_SEC_OFFSET, Math.clamp(duration - s, e, duration));
                    } else if (os < e) {
                        AL10.alSourcef(source, AL11.AL_SEC_OFFSET, e);
                    }
                }
            }
        };
    }

    public static class HornSound extends SlicedLoopingAutomobileSoundInstance {
        private final float hornPitch;

        public HornSound(Minecraft client, AutomobileEntity automobile, HornSoundDefinition hornSound, float pitch) {
            super(hornSound.sound().get(), client, automobile, hornSound.loopStart(), hornSound.loopEnd());
            this.hornPitch = pitch;
            this.continueSlicedLoop = true;
        }

        @Override
        protected boolean canPlay(AutomobileEntity automobile) {
            return !isStopped();
        }

        @Override
        public void tick() {
            super.tick();

            if (!this.automobile.honking()) {
                this.continueSlicedLoop = false;
            }
        }

        @Override
        protected float getPitch(AutomobileEntity automobile) {
            return hornPitch;
        }

        @Override
        protected float getVolume(AutomobileEntity automobile) {
            return 2 * automobile.getUnderwaterVolumeMultiplier();
        }

        @Override
        protected Vec3 getPosition(AutomobileEntity auto) {
            return auto.getHeadPos();
        }

        @Override
        protected double dopplerScale() {
            return 0.1;
        }
    }
}
