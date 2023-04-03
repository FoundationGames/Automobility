package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileData;
import io.github.foundationgames.automobility.automobile.AutomobilePrefab;
import io.github.foundationgames.automobility.automobile.AutomobileStats;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutomobileItem extends Item {
    public static final List<AutomobilePrefab> PREFABS = new ArrayList<>();
    private static final AutomobileData data = new AutomobileData();
    private static final AutomobileStats stats = new AutomobileStats();

    public AutomobileItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            var stack = context.getStack();
            data.read(stack.getOrCreateSubNbt("Automobile"));
            var e = new AutomobileEntity(AutomobilityEntities.AUTOMOBILE, context.getWorld());
            var pos = context.getHitPos();
            e.refreshPositionAndAngles(pos.x, pos.y, pos.z, context.getPlayerFacing().asRotation(), 0);
            e.setComponents(data.getFrame(), data.getWheel(), data.getEngine());
            context.getWorld().spawnEntity(e);
            stack.decrement(1);
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        data.read(stack.getOrCreateSubNbt("Automobile"));
        if (Screen.hasShiftDown()) {
            stats.from(data.getFrame(), data.getWheel(), data.getEngine());
            stats.appendTexts(tooltip, stats);
        } else {
            if (!data.isPrefab()) {
                tooltip.add(
                        Text.translatable("tooltip.automobility.frameLabel").formatted(Formatting.BLUE)
                                .append(Text.translatable(data.getFrame().getTranslationKey()).formatted(Formatting.DARK_GREEN))
                );
                tooltip.add(
                        Text.translatable("tooltip.automobility.wheelLabel").formatted(Formatting.BLUE)
                                .append(Text.translatable(data.getWheel().getTranslationKey()).formatted(Formatting.DARK_GREEN))
                );
                tooltip.add(
                        Text.translatable("tooltip.automobility.engineLabel").formatted(Formatting.BLUE)
                                .append(Text.translatable(data.getEngine().getTranslationKey()).formatted(Formatting.DARK_GREEN))
                );
            }
            tooltip.add(Text.translatable("tooltip.automobility.shiftForStats").formatted(Formatting.GOLD));
        }
    }

    public static void addPrefabs(AutomobilePrefab ... prefabs) {
        PREFABS.addAll(Arrays.asList(prefabs));
    }
}
