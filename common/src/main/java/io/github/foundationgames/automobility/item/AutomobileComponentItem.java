package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutomobileComponentItem<T extends AutomobileComponent<T>> extends Item implements CustomCreativeOutput {
    protected final String nbtKey;
    protected final String translationKey;
    protected final SimpleMapContentRegistry<T> registry;

    public AutomobileComponentItem(Properties settings, String nbtKey, String translationKey, SimpleMapContentRegistry<T> registry) {
        super(settings);
        this.nbtKey = nbtKey;
        this.translationKey = translationKey;
        this.registry = registry;
    }

    public ItemStack createStack(T component) {
        if (component.isEmpty()) {
            return ItemStack.EMPTY;
        }

        var stack = new ItemStack(this);
        this.setComponent(stack, component.getId());
        return stack;
    }

    public void setComponent(ItemStack stack, ResourceLocation component) {
        stack.getOrCreateTag().putString(this.nbtKey, component.toString());
    }

    public T getComponent(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(this.nbtKey)) {
            return this.registry.getOrDefault(ResourceLocation.tryParse(stack.getTag().getString(this.nbtKey)));
        }
        return this.registry.getOrDefault(null);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        var component = this.getComponent(stack);
        var id = component.getId();
        var compKey = id.getNamespace()+"."+id.getPath();
        tooltip.add(Component.translatable(this.translationKey+"."+compKey).withStyle(ChatFormatting.BLUE));

        component.appendTexts(tooltip, component);
    }

    @Override
    public void provideCreativeOutput(CreativeModeTab.Output output) {
        this.registry.forEach(component -> {
            if (addToCreative(component)) output.accept(this.createStack(component));
        });
    }

    public boolean isVisible(T component) {
        return !component.isEmpty();
    }

    protected boolean addToCreative(T component) {
        return !component.isEmpty();
    }
}
