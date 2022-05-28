package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.ToDoubleFunction;

public record DisplayStat<C extends StatContainer<C>>(String name, ToDoubleFunction<C> statProvider) {
    public static final TranslatableText STAT_SEPARATOR = new TranslatableText("char.automobility.statSeparator");

    public void appendTooltip(List<Text> tooltip, C container) {
        var compKey = container.getContainerTextKey();
        var statKey = "stat."+compKey+"."+this.name();
        tooltip.add(new TranslatableText(statKey).formatted(Formatting.AQUA)
                .append(STAT_SEPARATOR)
                .append(new TranslatableText(statKey+".readout",
                        AUtils.DEC_TWO_PLACES.format(this.statProvider().applyAsDouble(container))
                ).formatted(Formatting.GREEN)));
    }
}
