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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;

public class Automobility implements ModInitializer {
    public static final String MOD_ID = "automobility";

    public static final ItemGroup GROUP = FabricItemGroup.builder(id("automobility")).icon(AUtils::createGroupIcon).build();
    public static final ItemGroup COURSE_ELEMENTS = FabricItemGroup.builder(id("automobility_course_elements")).icon(AUtils::createCourseElementsIcon).build();
    public static final ItemGroup PREFABS = FabricItemGroup.builder(id("automobility_prefabs")).icon(AUtils::createPrefabsIcon).build();

    public static final TagKey<Block> SLOPES = TagKey.of(RegistryKeys.BLOCK, id("slopes"));
    public static final TagKey<Block> STEEP_SLOPES = TagKey.of(RegistryKeys.BLOCK, id("steep_slopes"));
    public static final TagKey<Block> NON_STEEP_SLOPES = TagKey.of(RegistryKeys.BLOCK, id("non_steep_slopes"));
    public static final TagKey<Block> STICKY_SLOPES = TagKey.of(RegistryKeys.BLOCK, id("sticky_slopes"));

    public static final ScreenHandlerType<AutoMechanicTableScreenHandler> AUTO_MECHANIC_SCREEN =
            Registry.register(Registries.SCREEN_HANDLER, Automobility.id("auto_mechanic_table"), new ScreenHandlerType<>(AutoMechanicTableScreenHandler::new));
    public static final ScreenHandlerType<SingleSlotScreenHandler> SINGLE_SLOT_SCREEN =
            Registry.register(Registries.SCREEN_HANDLER, Automobility.id("single_slot"), new ScreenHandlerType<>(SingleSlotScreenHandler::new));

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
        Registry.register(Registries.RECIPE_TYPE, AutoMechanicTableRecipe.ID, AutoMechanicTableRecipe.TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, AutoMechanicTableRecipe.ID, AutoMechanicTableRecipeSerializer.INSTANCE);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
