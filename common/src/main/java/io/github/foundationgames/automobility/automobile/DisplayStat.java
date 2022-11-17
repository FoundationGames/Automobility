package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.ToDoubleFunction;

public record DisplayStat<C extends StatContainer<C>>(String name, ToDoubleFunction<C> statProvider) {
    public static final Component STAT_SEPARATOR = Component.translatable("char.automobility.statSeparator");

    public void appendTooltip(List<Component> tooltip, C container) {
        var compKey = container.getContainerTextKey();
        var statKey = "stat."+compKey+"."+this.name();
        tooltip.add(Component.translatable(statKey).withStyle(ChatFormatting.AQUA)
                .append(STAT_SEPARATOR)
                .append(Component.translatable(statKey+".readout",
                        AUtils.DEC_TWO_PLACES.format(this.statProvider().applyAsDouble(container))
                ).withStyle(ChatFormatting.GREEN)));
    }
}
