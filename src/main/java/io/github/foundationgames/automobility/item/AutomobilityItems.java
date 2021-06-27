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
import net.minecraft.util.registry.Registry;

public enum AutomobilityItems {;
    public static final Item CROWBAR = register("crowbar", new Item(new Item.Settings().group(Automobility.GROUP)));
    public static final Item AUTOMOBILE = register("automobile", new AutomobileItem(new Item.Settings().group(Automobility.PREFABS)));

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
                new AutomobilePrefab(Automobility.id("c_arr"), AutomobileFrame.C_ARR, AutomobileWheel.CONVERTIBLE, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.id("dababy"), AutomobileFrame.DABABY, AutomobileWheel.CONVERTIBLE, AutomobileEngine.IRON)
        );
    }

    private static EntityRendererFactory.Context cachedCtx;
    private static final AutomobileDataReader reader = new AutomobileDataReader();
    private static final ItemRenderableAutomobile itemAutomobile = new ItemRenderableAutomobile(reader);

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        EntityRenderHelper.registerContextListener(ctx -> cachedCtx = ctx);
        BuiltinItemRendererRegistry.INSTANCE.register(AUTOMOBILE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            if (cachedCtx != null) {
                reader.read(stack.getOrCreateSubTag("Automobile"));
                float wheelDist = reader.getFrame().model().wheelSeparationLong();
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
