package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.util.Eventual;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreativeTabQueue implements CreativeModeTab.DisplayItemsGenerator {
    public final ResourceLocation location;
    private final List<Eventual<? extends Item>> items = new ArrayList<>();
    private final List<Supplier<ItemStack>> itemStacks = new ArrayList<>();

    public CreativeTabQueue(ResourceLocation location) {
        this.location = location;
    }

    public void queue(Eventual<? extends Item> item) {
        this.items.add(item);
    }

    public void queueStack(Supplier<ItemStack> itemStack) {
        this.itemStacks.add(itemStack);
    }

    @Override
    public void accept(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        items.forEach(i -> {
            if (i.require() instanceof CustomCreativeOutput outputItem) {
                outputItem.provideCreativeOutput(output, params.holders());
            } else {
                output.accept(i.require());
            }
        });

        itemStacks.forEach(i -> {
            output.accept(i.get());
        });
    }
}
