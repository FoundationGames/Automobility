package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.model.Model;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class AutomobileComponentItem<T extends AutomobileComponent<T>> extends Item {
    protected final String nbtKey;
    protected final String translationKey;
    protected final SimpleMapContentRegistry<T> registry;

    public AutomobileComponentItem(Settings settings, String nbtKey, String translationKey, SimpleMapContentRegistry<T> registry) {
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

    public void setComponent(ItemStack stack, Identifier component) {
        stack.getOrCreateNbt().putString(this.nbtKey, component.toString());
    }

    public T getComponent(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains(this.nbtKey)) {
            return this.registry.getOrDefault(Identifier.tryParse(stack.getNbt().getString(this.nbtKey)));
        }
        return this.registry.getOrDefault(null);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var component = this.getComponent(stack);
        var id = component.getId();
        var compKey = id.getNamespace()+"."+id.getPath();
        tooltip.add(new TranslatableText(this.translationKey+"."+compKey).formatted(Formatting.BLUE));

        component.appendTexts(tooltip, component);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            this.registry.forEach(component -> {
                if (addToCreative(component)) stacks.add(this.createStack(component));
            });
        }
    }

    
    protected boolean renders(T component) {
        return !component.isEmpty();
    }

    protected boolean addToCreative(T component) {
        return !component.isEmpty();
    }

    
    public void registerItemRenderer(Function<T, Model> modelProvider, Function<T, Identifier> textureProvider, ToFloatFunction<T> scaleProvider) {
        BuiltinItemRendererRegistry.INSTANCE.register(this, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            var component = this.getComponent(stack);
            if (this.renders(component)) {
                var model = modelProvider.apply(component);
                float scale = scaleProvider.apply(component);
                matrices.translate(0.5, 0, 0.5);
                matrices.scale(scale, -scale, -scale);
                model.render(matrices, vertexConsumers.getBuffer(model.getLayer(textureProvider.apply(component))), light, overlay, 1, 1, 1, 1);
            }
        });
    }
}
