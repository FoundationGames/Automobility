package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.item.AutomobilityItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public record AutomobilePrefab(Identifier id, AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {
    public ItemStack toStack() {
        var stack = new ItemStack(AutomobilityItems.AUTOMOBILE);
        var automobile = stack.getOrCreateSubTag("Automobile");
        automobile.putString("frame", frame().getId().toString());
        automobile.putString("wheels", wheel().getId().toString());
        automobile.putString("engine", engine().getId().toString());
        automobile.putBoolean("isPrefab", true);
        var display = stack.getOrCreateSubTag("display");
        display.putString("Name", String.format("{\"translate\":\"prefab.%s.%s\",\"italic\":\"false\"}", id().getNamespace(), id().getPath()));
        return stack;
    }
}
