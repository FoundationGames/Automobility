package io.github.foundationgames.automobility.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.foundationgames.automobility.item.AutomobileComponentItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashSet;

public class AutoMechanicTableRecipeSerializer implements RecipeSerializer<AutoMechanicTableRecipe> {
    public static final AutoMechanicTableRecipeSerializer INSTANCE = new AutoMechanicTableRecipeSerializer();

    public static ItemStack autoComponentStackFromJson(JsonObject obj) throws JsonSyntaxException, IllegalStateException {
        var id = ResourceLocation.tryParse(obj.get("item").getAsString());
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        var stack = BuiltInRegistries.ITEM.getOptional(id).map(i -> new ItemStack(i, count)).orElse(ItemStack.EMPTY);

        if (obj.has("component") && stack.getItem() instanceof AutomobileComponentItem<?> item) {
            var component = ResourceLocation.tryParse(obj.get("component").getAsString());
            if (component != null) {
                item.setComponent(stack, component);
            }
        }

        return stack;
    }

    @Override
    public AutoMechanicTableRecipe fromJson(ResourceLocation id, JsonObject json) {
        try {
            var category = ResourceLocation.tryParse(json.get("category").getAsString());
            var ingredients = new LinkedHashSet<Ingredient>();
            for (var ele : json.get("ingredients").getAsJsonArray()) {
                ingredients.add(Ingredient.fromJson(ele));
            }
            var result = autoComponentStackFromJson(json.get("result").getAsJsonObject());
            int sortNum = 0;
            if (json.has("sortnum")) {
                sortNum = json.get("sortnum").getAsInt();
            }

            return new AutoMechanicTableRecipe(id, category, ingredients, result, sortNum);
        } catch (IllegalStateException ex) {
            throw new JsonSyntaxException("Error parsing Auto Mechanic Table recipe - " + ex.getMessage());
        }
    }

    @Override
    public AutoMechanicTableRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        var category = ResourceLocation.tryParse(buf.readUtf());

        int size = buf.readByte();
        var ingredients = new LinkedHashSet<Ingredient>();
        for (int i = 0; i < size; i++) {
            ingredients.add(Ingredient.fromNetwork(buf));
        }

        var result = buf.readItem();
        int sortNum = buf.readInt();

        return new AutoMechanicTableRecipe(id, category, ingredients, result, sortNum);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, AutoMechanicTableRecipe recipe) {
        buf.writeUtf(recipe.category.toString());
        buf.writeByte(recipe.ingredients.size());
        recipe.ingredients.forEach(ing -> ing.toNetwork(buf));
        buf.writeItem(recipe.result);
        buf.writeInt(recipe.sortNum);
    }
}
