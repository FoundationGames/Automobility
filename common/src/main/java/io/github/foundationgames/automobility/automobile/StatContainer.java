package io.github.foundationgames.automobility.automobile;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public interface StatContainer<C extends StatContainer<C>> {
    ResourceLocation containerId();

    default String getContainerTextKey() {
        var id = this.containerId();
        return id.getNamespace()+"."+id.getPath();
    }

    void forEachStat(Consumer<DisplayStat<C>> action);

    default void appendTexts(List<Component> texts, C container) {
        this.forEachStat(stat -> stat.appendTooltip(texts, container));
    }
}
