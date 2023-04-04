package io.github.foundationgames.automobility.automobile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AutomobileData {
    private AutomobileFrame frame;
    private AutomobileWheel wheel;
    private AutomobileEngine engine;
    private boolean prefab;

    public AutomobileData() {
    }

    public void read(CompoundTag nbt) {
        frame = AutomobileFrame.REGISTRY.getOrDefault(ResourceLocation.tryParse(nbt.getString("frame")));
        wheel = AutomobileWheel.REGISTRY.getOrDefault(ResourceLocation.tryParse(nbt.getString("wheels")));
        engine = AutomobileEngine.REGISTRY.getOrDefault(ResourceLocation.tryParse(nbt.getString("engine")));
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
