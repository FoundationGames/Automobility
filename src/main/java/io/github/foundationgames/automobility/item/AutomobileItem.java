package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileDataReader;
import io.github.foundationgames.automobility.automobile.AutomobilePrefab;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutomobileItem extends Item {
    public static final List<AutomobilePrefab> PREFABS = new ArrayList<>();
    private static final AutomobileDataReader dataReader = new AutomobileDataReader();

    public AutomobileItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            dataReader.read(context.getStack().getOrCreateSubTag("Automobile"));
            var e = new AutomobileEntity(AutomobilityEntities.AUTOMOBILE, context.getWorld());
            var pos = context.getHitPos();
            e.refreshPositionAndAngles(pos.x, pos.y, pos.z, context.getPlayerFacing().asRotation(), 0);
            e.setComponents(dataReader.getFrame(), dataReader.getWheel(), dataReader.getEngine());
            context.getWorld().spawnEntity(e);
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    public static void addPrefabs(AutomobilePrefab ... prefabs) {
        PREFABS.addAll(Arrays.asList(prefabs));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group == getGroup() || group == ItemGroup.SEARCH || group == ItemGroup.TRANSPORTATION) {
            for (var prefab : PREFABS) {
                var stack = new ItemStack(this);
                var automobile = stack.getOrCreateSubTag("Automobile");
                automobile.putString("frame", prefab.frame().getId().toString());
                automobile.putString("wheels", prefab.wheel().getId().toString());
                automobile.putString("engine", prefab.engine().getId().toString());
                var display = stack.getOrCreateSubTag("display");
                display.putString("Name", String.format("{\"translate\":\"prefab.%s.%s\",\"italic\":\"false\"}", prefab.id().getNamespace(), prefab.id().getPath()));
                stacks.add(stack);
            }
        }
    }
}
