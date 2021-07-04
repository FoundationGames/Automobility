package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.*;
import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.automobile.render.item.ItemRenderableAutomobile;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

public enum AutomobilityItems {;
    public static final Item CROWBAR = register("crowbar", new TooltipItem(new TranslatableText("tooltip.item.automobility.crowbar").formatted(Formatting.BLUE), new Item.Settings().maxCount(1).group(Automobility.GROUP)));
    public static final Item AUTOMOBILE = register("automobile", new AutomobileItem(new Item.Settings().maxCount(1).group(Automobility.PREFABS)));

    public static void init() {
        AutomobileItem.addPrefabs(
                new AutomobilePrefab(Automobility.id("standard_white"), AutomobileFrame.STANDARD_WHITE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_orange"), AutomobileFrame.STANDARD_ORANGE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_magenta"), AutomobileFrame.STANDARD_MAGENTA, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_light_blue"), AutomobileFrame.STANDARD_LIGHT_BLUE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_yellow"), AutomobileFrame.STANDARD_YELLOW, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_lime"), AutomobileFrame.STANDARD_LIME, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_pink"), AutomobileFrame.STANDARD_PINK, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_gray"), AutomobileFrame.STANDARD_GRAY, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_light_gray"), AutomobileFrame.STANDARD_LIGHT_GRAY, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_cyan"), AutomobileFrame.STANDARD_CYAN, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_purple"), AutomobileFrame.STANDARD_PURPLE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_blue"), AutomobileFrame.STANDARD_BLUE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_brown"), AutomobileFrame.STANDARD_BROWN, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_green"), AutomobileFrame.STANDARD_GREEN, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_red"), AutomobileFrame.STANDARD_RED, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("standard_black"), AutomobileFrame.STANDARD_BLACK, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.id("red_tractor"), AutomobileFrame.RED_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.id("yellow_tractor"), AutomobileFrame.YELLOW_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.id("green_tractor"), AutomobileFrame.GREEN_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.id("blue_tractor"), AutomobileFrame.BLUE_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.id("shopping_cart"), AutomobileFrame.SHOPPING_CART, AutomobileWheel.STEEL, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.id("c_arr"), AutomobileFrame.C_ARR, AutomobileWheel.OFF_ROAD, AutomobileEngine.GOLD),
                new AutomobilePrefab(Automobility.id("pineapple"), AutomobileFrame.PINEAPPLE, AutomobileWheel.TRACTOR, AutomobileEngine.GOLD)
        );
    }

    @Environment(EnvType.CLIENT)
    private static EntityRendererFactory.Context cachedCtx;
    private static final AutomobileData reader = new AutomobileData();

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        var itemAutomobile = new ItemRenderableAutomobile(reader);
        EntityRenderHelper.registerContextListener(ctx -> cachedCtx = ctx);
        BuiltinItemRendererRegistry.INSTANCE.register(AUTOMOBILE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            if (cachedCtx != null) {
                reader.read(stack.getOrCreateSubTag("Automobile"));
                float wheelDist = reader.getFrame().model().lengthPx();
                float scale = 1;
                if (wheelDist > 16) {
                    scale = Math.max(0, scale - (wheelDist - 16) * 0.02f);
                }
                matrices.scale(scale, scale, scale);
                AutomobileRenderer.render(matrices, vertexConsumers, light, overlay, MinecraftClient.getInstance().getTickDelta(), reader.getFrame(), reader.getWheel(), reader.getEngine(), cachedCtx, itemAutomobile);
            }
        });
    }

    public static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, Automobility.id(name), item);
    }
}
