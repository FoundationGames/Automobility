package io.github.foundationgames.automobility.automobile;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class AutomobileDataReader {
    private AutomobileFrame frame;
    private AutomobileWheel wheel;
    private AutomobileEngine engine;

    public AutomobileDataReader() {
    }

    public void read(NbtCompound nbt) {
        frame = AutomobileFrame.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("frame")));
        wheel = AutomobileWheel.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("wheels")));
        engine = AutomobileEngine.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("engine")));
    }

    public AutomobileFrame getFrame() {
        return frame;
    }

    public AutomobileWheel getWheel() {
        return wheel;
    }

    public AutomobileEngine getEngine() {
        return engine;
    }
}
