package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class AutomobileStats implements StatContainer<AutomobileStats> {
    public static final ResourceLocation ID = Automobility.rl("automobile");
    public static final DisplayStat<AutomobileStats> STAT_ACCELERATION = new DisplayStat<>("acceleration", AutomobileStats::getAcceleration);
    public static final DisplayStat<AutomobileStats> STAT_COMFORTABLE_SPEED = new DisplayStat<>("comfortable_speed", stats -> stats.getComfortableSpeed() * 20);
    public static final DisplayStat<AutomobileStats> STAT_HANDLING = new DisplayStat<>("handling", AutomobileStats::getHandling);
    public static final DisplayStat<AutomobileStats> STAT_GRIP = new DisplayStat<>("grip", AutomobileStats::getGrip);

    private float acceleration = 0;     // 0-1
    private float comfortableSpeed = 0; // Blocks per Tick
    private float handling = 0;         // 0-1
    private float grip = 0;             // 0-1

    public AutomobileStats() {
    }

    public void from(AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {
        this.acceleration = ((1 - ((frame.weight() + wheel.size()) / 2)) + (2 * engine.torque()) / 3);
        this.comfortableSpeed = ((engine.speed() * 3) + ((engine.speed() * frame.weight()) * 2) + (engine.speed() * wheel.size())) / 5.7f;
        this.handling = ((1 - wheel.size()) + (1 - frame.weight()) + 2) / 4;
        this.grip = (wheel.grip() + frame.weight()) / 2;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getComfortableSpeed() {
        return comfortableSpeed;
    }

    public float getHandling() {
        return handling;
    }

    public float getGrip() {
        return grip;
    }

    @Override
    public ResourceLocation containerId() {
        return ID;
    }

    @Override
    public void forEachStat(Consumer<DisplayStat<AutomobileStats>> action) {
        action.accept(STAT_ACCELERATION);
        action.accept(STAT_COMFORTABLE_SPEED);
        action.accept(STAT_HANDLING);
        action.accept(STAT_GRIP);
    }
}
