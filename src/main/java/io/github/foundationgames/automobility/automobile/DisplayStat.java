package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.ToDoubleFunction;

public record DisplayStat<C extends StatContainer<C>>(String name, ToDoubleFunction<C> statProvider) {
    public static final Text STAT_SEPARATOR = Text.translatable("char.automobility.statSeparator");

    public void appendTooltip(List<Text> tooltip, C container) {
        var compKey = container.getContainerTextKey();
        var statKey = "stat."+compKey+"."+this.name();
        tooltip.add(Text.translatable(statKey).formatted(Formatting.AQUA)
                .append(STAT_SEPARATOR)
                .append(Text.translatable(statKey+".readout",
                        AUtils.DEC_TWO_PLACES.format(this.statProvider().applyAsDouble(container))
                ).formatted(Formatting.GREEN)));
    }
}
