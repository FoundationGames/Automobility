package io.github.foundationgames.automobility.automobile;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public interface StatContainer<C extends StatContainer<C>> {
    Identifier containerId();

    default String getContainerTextKey() {
        var id = this.containerId();
        return id.getNamespace()+"."+id.getPath();
    }

    void forEachStat(Consumer<DisplayStat<C>> action);

    default void appendTexts(List<Text> texts, C container) {
        this.forEachStat(stat -> stat.appendTooltip(texts, container));
    }
}
