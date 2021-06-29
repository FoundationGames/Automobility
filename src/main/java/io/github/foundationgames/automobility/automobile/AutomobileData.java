package io.github.foundationgames.automobility.automobile;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class AutomobileData {
    private AutomobileFrame frame;
    private AutomobileWheel wheel;
    private AutomobileEngine engine;
    private boolean prefab;

    public AutomobileData() {
    }

    public void read(NbtCompound nbt) {
        frame = AutomobileFrame.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("frame")));
        wheel = AutomobileWheel.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("wheels")));
        engine = AutomobileEngine.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("engine")));
        prefab = nbt.contains("isPrefab") && nbt.getBoolean("isPrefab");
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

    public boolean isPrefab() {
        return prefab;
    }
}
