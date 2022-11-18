package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileData;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobilePrefab;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.render.AutomobileRenderer;
import io.github.foundationgames.automobility.automobile.render.item.ItemRenderableAutomobile;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import io.github.foundationgames.automobility.util.Eventual;
import io.github.foundationgames.automobility.util.RegistryQueue;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public enum AutomobilityItems {;
    public static final Eventual<Item> CROWBAR = register("crowbar", () -> new TooltipItem(Component.translatable("tooltip.item.automobility.crowbar").withStyle(ChatFormatting.BLUE), new Item.Properties().stacksTo(1).tab(Automobility.GROUP)));
    public static final Eventual<Item> AUTOMOBILE = register("automobile", () -> new AutomobileItem(new Item.Properties().stacksTo(1).tab(Automobility.PREFABS)));
    public static final Eventual<AutomobileFrameItem> AUTOMOBILE_FRAME = register("automobile_frame", () -> new AutomobileFrameItem(new Item.Properties().stacksTo(16).tab(Automobility.GROUP)));
    public static final Eventual<AutomobileWheelItem> AUTOMOBILE_WHEEL = register("automobile_wheel", () -> new AutomobileWheelItem(new Item.Properties().tab(Automobility.GROUP)));
    public static final Eventual<AutomobileEngineItem> AUTOMOBILE_ENGINE = register("automobile_engine", () -> new AutomobileEngineItem(new Item.Properties().stacksTo(16).tab(Automobility.GROUP)));
    public static final Eventual<FrontAttachmentItem> FRONT_ATTACHMENT = register("front_attachment", () -> new FrontAttachmentItem(new Item.Properties().stacksTo(1).tab(Automobility.GROUP)));
    public static final Eventual<RearAttachmentItem> REAR_ATTACHMENT = register("rear_attachment", () -> new RearAttachmentItem(new Item.Properties().stacksTo(1).tab(Automobility.GROUP)));

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

    @OnlyIn(Dist.CLIENT) private static EntityRendererProvider.Context cachedCtx;
    @OnlyIn(Dist.CLIENT) private static final Map<AutomobileFrame, Model> frameModelPool = new HashMap<>();
    @OnlyIn(Dist.CLIENT) private static final Map<AutomobileWheel, Model> wheelModelPool = new HashMap<>();
    @OnlyIn(Dist.CLIENT) private static final Map<AutomobileEngine, Model> engineModelPool = new HashMap<>();
    @OnlyIn(Dist.CLIENT) private static final Map<RearAttachmentType<?>, Model> rearAttModelPool = new HashMap<>();
    @OnlyIn(Dist.CLIENT) private static final Map<FrontAttachmentType<?>, Model> frontAttModelPool = new HashMap<>();

    private static final AutomobileData reader = new AutomobileData();

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        var itemAutomobile = new ItemRenderableAutomobile(reader);
        EntityRenderHelper.registerContextListener(ctx -> {
            cachedCtx = ctx;
            rearAttModelPool.clear();
        });

        Platform.get().builtinItemRenderer(AUTOMOBILE.require(), (stack, type, pose, buffers, light, overlay) -> {
            if (cachedCtx != null) {
                reader.read(stack.getOrCreateTagElement("Automobile"));
                float wheelDist = reader.getFrame().model().lengthPx() / 16;
                float scale = 1;
                scale /= wheelDist * 0.77f;
                pose.scale(scale, scale, scale);
                AutomobileRenderer.render(pose, buffers, light, overlay, Minecraft.getInstance().getFrameTime(), cachedCtx, itemAutomobile);
            }
        });
        AUTOMOBILE_FRAME.require().registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), frameModelPool),
                t -> t.model().texture(), t -> 1 / ((t.model().lengthPx() / 16) * 0.77f)
        );
        AUTOMOBILE_WHEEL.require().registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), wheelModelPool),
                t -> t.model().texture(), t -> 6 / t.model().radius()
        );
        AUTOMOBILE_ENGINE.require().registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), engineModelPool),
                t -> t.model().texture(), t -> 1
        );
        REAR_ATTACHMENT.require().registerItemRenderer(
                pooledModelProvider(t -> t.model().model().apply(cachedCtx), rearAttModelPool),
                t -> t.model().texture(), t -> 1
        );
        FRONT_ATTACHMENT.require().registerItemRenderer(
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

    public static <T extends Item> Eventual<T> register(String name, Supplier<T> item) {
        return RegistryQueue.register(Registry.ITEM, Automobility.rl(name), item);
    }
}
