package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.*;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.automobile.render.item.ItemRenderableAutomobile;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum AutomobilityItems {;
    public static final Item CROWBAR = register("crowbar", new TooltipItem(Component.translatable("tooltip.item.automobility.crowbar").withStyle(ChatFormatting.BLUE), new Item.Properties().stacksTo(1).tab(Automobility.GROUP)));
    public static final Item AUTOMOBILE = register("automobile", new AutomobileItem(new Item.Properties().stacksTo(1).tab(Automobility.PREFABS)));
    public static final AutomobileFrameItem AUTOMOBILE_FRAME = register("automobile_frame", new AutomobileFrameItem(new Item.Properties().stacksTo(16).tab(Automobility.GROUP)));
    public static final AutomobileWheelItem AUTOMOBILE_WHEEL = register("automobile_wheel", new AutomobileWheelItem(new Item.Properties().tab(Automobility.GROUP)));
    public static final AutomobileEngineItem AUTOMOBILE_ENGINE = register("automobile_engine", new AutomobileEngineItem(new Item.Properties().stacksTo(16).tab(Automobility.GROUP)));
    public static final FrontAttachmentItem FRONT_ATTACHMENT = register("front_attachment", new FrontAttachmentItem(new Item.Properties().stacksTo(1).tab(Automobility.GROUP)));
    public static final RearAttachmentItem REAR_ATTACHMENT = register("rear_attachment", new RearAttachmentItem(new Item.Properties().stacksTo(1).tab(Automobility.GROUP)));

    public static void init() {
        AutomobileItem.addPrefabs(
                new AutomobilePrefab(Automobility.rl("wooden_motorcar"), AutomobileFrame.WOODEN_MOTORCAR, AutomobileWheel.CARRIAGE, AutomobileEngine.STONE),
                new AutomobilePrefab(Automobility.rl("copper_motorcar"), AutomobileFrame.COPPER_MOTORCAR, AutomobileWheel.PLATED, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.rl("steel_motorcar"), AutomobileFrame.STEEL_MOTORCAR, AutomobileWheel.STREET, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("golden_motorcar"), AutomobileFrame.GOLDEN_MOTORCAR, AutomobileWheel.GILDED, AutomobileEngine.GOLD),
                new AutomobilePrefab(Automobility.rl("bejeweled_motorcar"), AutomobileFrame.BEJEWELED_MOTORCAR, AutomobileWheel.BEJEWELED, AutomobileEngine.DIAMOND),
                new AutomobilePrefab(Automobility.rl("standard_white"), AutomobileFrame.STANDARD_WHITE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_orange"), AutomobileFrame.STANDARD_ORANGE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_magenta"), AutomobileFrame.STANDARD_MAGENTA, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_light_blue"), AutomobileFrame.STANDARD_LIGHT_BLUE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_yellow"), AutomobileFrame.STANDARD_YELLOW, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_lime"), AutomobileFrame.STANDARD_LIME, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_pink"), AutomobileFrame.STANDARD_PINK, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_gray"), AutomobileFrame.STANDARD_GRAY, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_light_gray"), AutomobileFrame.STANDARD_LIGHT_GRAY, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_cyan"), AutomobileFrame.STANDARD_CYAN, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_purple"), AutomobileFrame.STANDARD_PURPLE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_blue"), AutomobileFrame.STANDARD_BLUE, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_brown"), AutomobileFrame.STANDARD_BROWN, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_green"), AutomobileFrame.STANDARD_GREEN, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_red"), AutomobileFrame.STANDARD_RED, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("standard_black"), AutomobileFrame.STANDARD_BLACK, AutomobileWheel.STANDARD, AutomobileEngine.IRON),
                new AutomobilePrefab(Automobility.rl("amethyst_rickshaw"), AutomobileFrame.AMETHYST_RICKSHAW, AutomobileWheel.BEJEWELED, AutomobileEngine.STONE),
                new AutomobilePrefab(Automobility.rl("quartz_rickshaw"), AutomobileFrame.QUARTZ_RICKSHAW, AutomobileWheel.GILDED, AutomobileEngine.GOLD),
                new AutomobilePrefab(Automobility.rl("prismarine_rickshaw"), AutomobileFrame.PRISMARINE_RICKSHAW, AutomobileWheel.PLATED, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.rl("echo_rickshaw"), AutomobileFrame.ECHO_RICKSHAW, AutomobileWheel.STREET, AutomobileEngine.DIAMOND),
                new AutomobilePrefab(Automobility.rl("red_tractor"), AutomobileFrame.RED_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.rl("yellow_tractor"), AutomobileFrame.YELLOW_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.rl("green_tractor"), AutomobileFrame.GREEN_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.rl("blue_tractor"), AutomobileFrame.BLUE_TRACTOR, AutomobileWheel.TRACTOR, AutomobileEngine.COPPER),
                new AutomobilePrefab(Automobility.rl("shopping_cart"), AutomobileFrame.SHOPPING_CART, AutomobileWheel.STEEL, AutomobileEngine.STONE),
                new AutomobilePrefab(Automobility.rl("c_arr"), AutomobileFrame.C_ARR, AutomobileWheel.OFF_ROAD, AutomobileEngine.DIAMOND),
                new AutomobilePrefab(Automobility.rl("pineapple"), AutomobileFrame.PINEAPPLE, AutomobileWheel.TRACTOR, AutomobileEngine.GOLD)
        );
    }

    @Environment(EnvType.CLIENT) private static EntityRendererProvider.Context cachedCtx;
    @Environment(EnvType.CLIENT) private static final Map<AutomobileFrame, Model> frameModelPool = new HashMap<>();
    @Environment(EnvType.CLIENT) private static final Map<AutomobileWheel, Model> wheelModelPool = new HashMap<>();
    @Environment(EnvType.CLIENT) private static final Map<AutomobileEngine, Model> engineModelPool = new HashMap<>();
    @Environment(EnvType.CLIENT) private static final Map<RearAttachmentType<?>, Model> rearAttModelPool = new HashMap<>();
    @Environment(EnvType.CLIENT) private static final Map<FrontAttachmentType<?>, Model> frontAttModelPool = new HashMap<>();

    private static final AutomobileData reader = new AutomobileData();

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        var itemAutomobile = new ItemRenderableAutomobile(reader);
        EntityRenderHelper.registerContextListener(ctx -> {
            cachedCtx = ctx;
            rearAttModelPool.clear();
        });

        BuiltinItemRendererRegistry.INSTANCE.register(AUTOMOBILE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            if (cachedCtx != null) {
                reader.read(stack.getOrCreateTagElement("Automobile"));
                float wheelDist = reader.getFrame().model().lengthPx() / 16;
                float scale = 1;
                scale /= wheelDist * 0.77f;
                matrices.scale(scale, scale, scale);
                AutomobileRenderer.render(matrices, vertexConsumers, light, overlay, Minecraft.getInstance().getFrameTime(), cachedCtx, itemAutomobile);
            }
        });
        AUTOMOBILE_FRAME.registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), frameModelPool),
                t -> t.model().texture(), t -> 1 / ((t.model().lengthPx() / 16) * 0.77f)
        );
        AUTOMOBILE_WHEEL.registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), wheelModelPool),
                t -> t.model().texture(), t -> 6 / t.model().radius()
        );
        AUTOMOBILE_ENGINE.registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), engineModelPool),
                t -> t.model().texture(), t -> 1
        );
        REAR_ATTACHMENT.registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), rearAttModelPool),
                t -> t.model().texture(), t -> 1
        );
        FRONT_ATTACHMENT.registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), frontAttModelPool),
                t -> t.model().texture(), t -> t.model().scale()
        );
    }

    private static <T extends SimpleMapContentRegistry.Identifiable> Function<T, Model> pooledModelProvider(Function<T, Model> provider, Map<T, Model> pool) {
        return t -> {
            if (!pool.containsKey(t)) {
                var model = provider.apply(t);
                pool.put(t, model);
                return model;
            }
            return pool.get(t);
        };
    }

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registry.ITEM, Automobility.rl(name), item);
    }
}
