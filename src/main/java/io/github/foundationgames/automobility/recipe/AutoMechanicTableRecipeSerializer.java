package io.github.foundationgames.automobility.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.foundationgames.automobility.item.AutomobileComponentItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class AutoMechanicTableRecipeSerializer implements RecipeSerializer<AutoMechanicTableRecipe> {
    public static final AutoMechanicTableRecipeSerializer INSTANCE = new AutoMechanicTableRecipeSerializer();

    public static ItemStack autoComponentStackFromJson(JsonObject obj) throws JsonSyntaxException, IllegalStateException {
        var id = Identifier.tryParse(obj.get("item").getAsString());
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        var stack = Registry.ITEM.getOrEmpty(id).map(i -> new ItemStack(i, count)).orElse(ItemStack.EMPTY);

        if (obj.has("component") && stack.getItem() instanceof AutomobileComponentItem<?> item) {
            var component = Identifier.tryParse(obj.get("component").getAsString());
            if (component != null) {
                item.setComponent(stack, component);
            }
        }

        return stack;
    }

    @Override
    public AutoMechanicTableRecipe read(Identifier id, JsonObject json) {
        try {
            var category = Identifier.tryParse(json.get("category").getAsString());
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
    public AutoMechanicTableRecipe read(Identifier id, PacketByteBuf buf) {
        var category = Identifier.tryParse(buf.readString());

        int size = buf.readByte();
        var ingredients = new LinkedHashSet<Ingredient>();
        for (int i = 0; i < size; i++) {
            ingredients.add(Ingredient.fromPacket(buf));
        }

        var result = buf.readItemStack();
        int sortNum = buf.readInt();

        return new AutoMechanicTableRecipe(id, category, ingredients, result, sortNum);
    }

    @Override
    public void write(PacketByteBuf buf, AutoMechanicTableRecipe recipe) {
        buf.writeString(recipe.category.toString());
        buf.writeByte(recipe.ingredients.size());
        recipe.ingredients.forEach(ing -> ing.write(buf));
        buf.writeItemStack(recipe.result);
        buf.writeInt(recipe.sortNum);
    }
}
