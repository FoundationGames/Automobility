package io.github.foundationgames.automobility.recipe;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AutoMechanicTableRecipe implements Recipe<SimpleContainer>, Comparable<AutoMechanicTableRecipe> {
    public static final ResourceLocation ID = Automobility.rl("auto_mechanic_table");
    public static final RecipeType<AutoMechanicTableRecipe> TYPE = new RecipeType<>() {};
    private final ResourceLocation id;

    protected final ResourceLocation category;
    protected final Set<Ingredient> ingredients;
    protected final ItemStack result;
    protected final int sortNum;

    public AutoMechanicTableRecipe(ResourceLocation id, ResourceLocation category, Set<Ingredient> ingredients, ItemStack result, int sortNum) {
        this.id = id;
        this.category = category;
        this.ingredients = ingredients;
        this.result = result;
        this.sortNum = sortNum;
    }

    public ResourceLocation getCategory() {
        return this.category;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level lvl) {
        boolean[] result = {true};
        this.forMissingIngredients(inv, ing -> result[0] = false);

        return result[0];
    }

    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess var2) {
        return assemble(inv);
    }

    public ItemStack assemble(SimpleContainer inv) {
        for (var ing : this.ingredients) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                var stack = inv.getItem(i);
                if (ing.test(stack)) {
                    stack.shrink(1);
                    break;
                }
            }
        }

        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess var1) {
        return getResultItem();
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AutoMechanicTableRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    public void forMissingIngredients(Container inv, Consumer<Ingredient> action) {
        var invCopy = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            invCopy.add(inv.getItem(i));
        }

        for (var ing : this.ingredients) {
            if (invCopy.stream().noneMatch(ing)) {
                action.accept(ing);
            } else {
                invCopy.remove(invCopy.stream().filter(ing).collect(Collectors.toList()).get(0));
            }
        }
    }

    @Override
    public int compareTo(@NotNull AutoMechanicTableRecipe o) {
        int diff = this.getCategory().compareTo(o.getCategory());
        if (diff != 0) return diff;

        diff = Integer.compare(this.sortNum, o.sortNum);
        if (diff != 0) return diff;

        return this.getId().compareTo(o.getId());
    }
}
