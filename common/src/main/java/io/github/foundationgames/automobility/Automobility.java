package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.item.CreativeTabQueue;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipe;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipeSerializer;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreenHandler;
import io.github.foundationgames.automobility.screen.SingleSlotScreenHandler;
import io.github.foundationgames.automobility.sound.AutomobilitySounds;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.Eventual;
import io.github.foundationgames.automobility.util.InitlessConstants;
import io.github.foundationgames.automobility.util.RegistryQueue;
import io.github.foundationgames.automobility.util.network.CommonPackets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

public class Automobility {
    public static final String MOD_ID = InitlessConstants.AUTOMOBILITY;

    public static CreativeTabQueue TAB = new CreativeTabQueue(rl("automobility"));
    public static CreativeTabQueue PREFAB_TAB = new CreativeTabQueue(rl("automobility_prefabs"));

    public static final TagKey<Block> SLOPES = TagKey.create(Registries.BLOCK, rl("slopes"));
    public static final TagKey<Block> STEEP_SLOPES = TagKey.create(Registries.BLOCK, rl("steep_slopes"));
    public static final TagKey<Block> NON_STEEP_SLOPES = TagKey.create(Registries.BLOCK, rl("non_steep_slopes"));
    public static final TagKey<Block> STICKY_SLOPES = TagKey.create(Registries.BLOCK, rl("sticky_slopes"));

    public static final Eventual<MenuType<AutoMechanicTableScreenHandler>> AUTO_MECHANIC_SCREEN =
            RegistryQueue.register(BuiltInRegistries.MENU, Automobility.rl("auto_mechanic_table"), () -> Platform.get().menuType(AutoMechanicTableScreenHandler::new));
    public static final Eventual<MenuType<SingleSlotScreenHandler>> SINGLE_SLOT_SCREEN =
            RegistryQueue.register(BuiltInRegistries.MENU, Automobility.rl("single_slot"), () -> Platform.get().menuType(SingleSlotScreenHandler::new));

    public static void init() {
        AutomobilitySounds.init();
        AutomobilityBlocks.init();
        AutomobilityItems.init();
        AutomobilityEntities.init();
        AutomobilityParticles.init();
        initOther();

        CommonPackets.init();
    }

    public static void initOther() {
        RegistryQueue.register(BuiltInRegistries.RECIPE_TYPE, AutoMechanicTableRecipe.ID, () -> AutoMechanicTableRecipe.TYPE);
        RegistryQueue.register(BuiltInRegistries.RECIPE_SERIALIZER, AutoMechanicTableRecipe.ID, () -> AutoMechanicTableRecipeSerializer.INSTANCE);
        RegistryQueue.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB.location, () -> Platform.get().creativeTab(TAB.location, AUtils::createGroupIcon, TAB));
        RegistryQueue.register(BuiltInRegistries.CREATIVE_MODE_TAB, PREFAB_TAB.location, () -> Platform.get().creativeTab(PREFAB_TAB.location, AUtils::createPrefabsIcon, PREFAB_TAB));
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
