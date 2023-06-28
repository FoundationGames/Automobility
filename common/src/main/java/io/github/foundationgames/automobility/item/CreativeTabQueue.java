package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.util.Eventual;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class CreativeTabQueue implements CreativeModeTab.DisplayItemsGenerator {
    public final ResourceLocation location;
    private final List<Eventual<? extends Item>> items = new ArrayList<>();

    public CreativeTabQueue(ResourceLocation location) {
        this.location = location;
    }

    public void queue(Eventual<? extends Item> item) {
        this.items.add(item);
    }

    @Override
    public void accept(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        items.forEach(i -> {
            if (i.require() instanceof CustomCreativeOutput outputItem) {
                outputItem.provideCreativeOutput(output);
            } else {
                output.accept(i.require());
            }
        });
    }
}
