package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileData;
import io.github.foundationgames.automobility.automobile.AutomobilePrefab;
import io.github.foundationgames.automobility.automobile.AutomobileStats;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutomobileItem extends Item implements CustomCreativeOutput {
    public static final List<AutomobilePrefab> PREFABS = new ArrayList<>();
    private static final AutomobileData data = new AutomobileData();
    private static final AutomobileStats stats = new AutomobileStats();

    public AutomobileItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            var stack = context.getItemInHand();
            data.read(stack.getOrCreateTagElement("Automobile"));
            var e = new AutomobileEntity(AutomobilityEntities.AUTOMOBILE.require(), context.getLevel());
            var pos = context.getClickLocation();
            e.moveTo(pos.x, pos.y, pos.z, context.getHorizontalDirection().toYRot(), 0);
            e.setComponents(data.getFrame(), data.getWheel(), data.getEngine());
            context.getLevel().addFreshEntity(e);
            stack.shrink(1);
            return InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        data.read(stack.getOrCreateTagElement("Automobile"));
        if (Screen.hasShiftDown()) {
            stats.from(data.getFrame(), data.getWheel(), data.getEngine());
            stats.appendTexts(tooltip, stats);
        } else {
            if (!data.isPrefab()) {
                tooltip.add(
                        Component.translatable("tooltip.automobility.frameLabel").withStyle(ChatFormatting.BLUE)
                                .append(Component.translatable(data.getFrame().getTranslationKey()).withStyle(ChatFormatting.DARK_GREEN))
                );
                tooltip.add(
                        Component.translatable("tooltip.automobility.wheelLabel").withStyle(ChatFormatting.BLUE)
                                .append(Component.translatable(data.getWheel().getTranslationKey()).withStyle(ChatFormatting.DARK_GREEN))
                );
                tooltip.add(
                        Component.translatable("tooltip.automobility.engineLabel").withStyle(ChatFormatting.BLUE)
                                .append(Component.translatable(data.getEngine().getTranslationKey()).withStyle(ChatFormatting.DARK_GREEN))
                );
            }
            tooltip.add(Component.translatable("tooltip.automobility.shiftForStats").withStyle(ChatFormatting.GOLD));
        }
    }

    public static void addPrefabs(AutomobilePrefab ... prefabs) {
        PREFABS.addAll(Arrays.asList(prefabs));
    }

    @Override
    public void provideCreativeOutput(CreativeModeTab.Output output) {
        for (var prefab : PREFABS) {
            output.accept(prefab.toStack());
        }
    }
}
