package io.github.foundationgames.automobility.recipe;

import io.github.foundationgames.automobility.Automobility;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AutoMechanicTableRecipe implements Recipe<SimpleInventory>, Comparable<AutoMechanicTableRecipe> {
    public static final Identifier ID = Automobility.id("auto_mechanic_table");
    public static final RecipeType<AutoMechanicTableRecipe> TYPE = new RecipeType<>() {};
    private final Identifier id;

    protected final Identifier category;
    protected final Set<Ingredient> ingredients;
    protected final ItemStack result;
    protected final int sortNum;

    public AutoMechanicTableRecipe(Identifier id, Identifier category, Set<Ingredient> ingredients, ItemStack result, int sortNum) {
        this.id = id;
        this.category = category;
        this.ingredients = ingredients;
        this.result = result;
        this.sortNum = sortNum;
    }

    public Identifier getCategory() {
        return this.category;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        boolean[] result = {true};
        this.forMissingIngredients(inventory, ing -> result[0] = false);

        return result[0];
    }

    @Override
    public ItemStack craft(SimpleInventory inv) {
        for (var ing : this.ingredients) {
            for (int i = 0; i < inv.size(); i++) {
                var stack = inv.getStack(i);
                if (ing.test(stack)) {
                    stack.decrement(1);
                    break;
                }
            }
        }

        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return this.result;
    }

    @Override
    public Identifier getId() {
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

    public void forMissingIngredients(Inventory inv, Consumer<Ingredient> action) {
        var invCopy = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.size(); i++) {
            invCopy.add(inv.getStack(i));
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
