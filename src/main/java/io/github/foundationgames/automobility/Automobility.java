package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipe;
import io.github.foundationgames.automobility.recipe.AutoMechanicTableRecipeSerializer;
import io.github.foundationgames.automobility.resource.AutomobilityData;
import io.github.foundationgames.automobility.screen.AutoMechanicTableScreenHandler;
import io.github.foundationgames.automobility.screen.SingleSlotScreenHandler;
import io.github.foundationgames.automobility.sound.AutomobilitySounds;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.midnightcontrols.ControllerUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class Automobility implements ModInitializer {
    public static final String MOD_ID = "automobility";

    public static final CreativeModeTab GROUP = FabricItemGroupBuilder.build(rl("automobility"), AUtils::createGroupIcon);
    public static final CreativeModeTab COURSE_ELEMENTS = FabricItemGroupBuilder.build(rl("automobility_course_elements"), AUtils::createCourseElementsIcon);
    public static final CreativeModeTab PREFABS = FabricItemGroupBuilder.build(rl("automobility_prefabs"), AUtils::createPrefabsIcon);

    public static final TagKey<Block> SLOPES = TagKey.create(Registry.BLOCK_REGISTRY, rl("slopes"));
    public static final TagKey<Block> STEEP_SLOPES = TagKey.create(Registry.BLOCK_REGISTRY, rl("steep_slopes"));
    public static final TagKey<Block> NON_STEEP_SLOPES = TagKey.create(Registry.BLOCK_REGISTRY, rl("non_steep_slopes"));
    public static final TagKey<Block> STICKY_SLOPES = TagKey.create(Registry.BLOCK_REGISTRY, rl("sticky_slopes"));

    public static final MenuType<AutoMechanicTableScreenHandler> AUTO_MECHANIC_SCREEN =
            Registry.register(Registry.MENU, Automobility.rl("auto_mechanic_table"), new MenuType<>(AutoMechanicTableScreenHandler::new));
    public static final MenuType<SingleSlotScreenHandler> SINGLE_SLOT_SCREEN =
            Registry.register(Registry.MENU, Automobility.rl("single_slot"), new MenuType<>(SingleSlotScreenHandler::new));

    @Override
    public void onInitialize() {
        AutomobilityBlocks.init();
        AutomobilityItems.init();
        AutomobilityEntities.init();
        AutomobilityParticles.init();
        AutomobilitySounds.init();
        initOther();

        PayloadPackets.init();
        AutomobilityData.setup();
        ControllerUtils.initMidnightControlsHandler();
    }

    public static void initOther() {
        Registry.register(Registry.RECIPE_TYPE, AutoMechanicTableRecipe.ID, AutoMechanicTableRecipe.TYPE);
        Registry.register(Registry.RECIPE_SERIALIZER, AutoMechanicTableRecipe.ID, AutoMechanicTableRecipeSerializer.INSTANCE);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
