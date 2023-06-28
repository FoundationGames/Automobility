package io.github.foundationgames.automobility.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.foundationgames.automobility.util.HexCons;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BEWLRs {
    private static final Map<Item, HexCons<ItemStack, ItemDisplayContext, PoseStack, MultiBufferSource, Integer, Integer>> BEWLRS = new HashMap<>();

    public static void add(Item item, HexCons<ItemStack, ItemDisplayContext, PoseStack, MultiBufferSource, Integer, Integer> renderer) {
        BEWLRS.put(item, renderer);
    }

    /**
     * @return {@code true} to cancel the rest of the BEWLR item rendering
     */
    public static boolean tryRender(ItemStack stack, ItemDisplayContext transform, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        var item = stack.getItem();
        if (BEWLRS.containsKey(item)) {
            BEWLRS.get(item).accept(stack, transform, pose, buffers, light, overlay);
            return true;
        }

        return false;
    }
}
