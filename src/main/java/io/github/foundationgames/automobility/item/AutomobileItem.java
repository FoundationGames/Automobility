package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.*;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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

    public AutomobileItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            data.read(context.getStack().getOrCreateSubTag("Automobile"));
            var e = new AutomobileEntity(AutomobilityEntities.AUTOMOBILE, context.getWorld());
            var pos = context.getHitPos();
            e.refreshPositionAndAngles(pos.x, pos.y, pos.z, context.getPlayerFacing().asRotation(), 0);
            e.setComponents(data.getFrame(), data.getWheel(), data.getEngine());
            context.getWorld().spawnEntity(e);
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        data.read(stack.getOrCreateSubTag("Automobile"));
        if (!data.isPrefab()) {
            tooltip.add(
                    new TranslatableText("tooltip.automobility.frameLabel").formatted(Formatting.BLUE)
                            .append(new TranslatableText(data.getFrame().getTranslationKey()).formatted(Formatting.DARK_GREEN))
            );
            tooltip.add(
                    new TranslatableText("tooltip.automobility.wheelLabel").formatted(Formatting.BLUE)
                            .append(new TranslatableText(data.getWheel().getTranslationKey()).formatted(Formatting.DARK_GREEN))
            );
            tooltip.add(
                    new TranslatableText("tooltip.automobility.engineLabel").formatted(Formatting.BLUE)
                            .append(new TranslatableText(data.getEngine().getTranslationKey()).formatted(Formatting.DARK_GREEN))
            );
        }
    }

    public static void addPrefabs(AutomobilePrefab ... prefabs) {
        PREFABS.addAll(Arrays.asList(prefabs));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (isIn(group) || group == ItemGroup.SEARCH || group == ItemGroup.TRANSPORTATION) {
            for (var prefab : PREFABS) {
                stacks.add(prefab.toStack());
            }
        }
        if (group == ItemGroup.SEARCH) {
            AutomobileFrame.REGISTRY.forEach(frame -> AutomobileEngine.REGISTRY.forEach(engine -> AutomobileWheel.REGISTRY.forEach(wheel -> {
                var stack = new ItemStack(AutomobilityItems.AUTOMOBILE);
                var automobile = stack.getOrCreateSubTag("Automobile");
                automobile.putString("frame", frame.getId().toString());
                automobile.putString("wheels", wheel.getId().toString());
                automobile.putString("engine", engine.getId().toString());
                stacks.add(stack);
            })));
        }
    }
}
