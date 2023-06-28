package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.item.DynamicCreativeItem;
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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Automobility {
    public static final String MOD_ID = InitlessConstants.AUTOMOBILITY;

    public static ItemGroup GROUP = new ItemGroup(rl("automobility"));
    public static ItemGroup PREFABS = new ItemGroup(rl("automobility_prefabs"));

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
        RegistryQueue.register(BuiltInRegistries.CREATIVE_MODE_TAB, GROUP.rl, () -> Platform.get().creativeTab(GROUP.rl, AUtils::createGroupIcon, GROUP));
        RegistryQueue.register(BuiltInRegistries.CREATIVE_MODE_TAB, PREFABS.rl, () -> Platform.get().creativeTab(PREFABS.rl, AUtils::createPrefabsIcon, PREFABS));
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static void addToItemGroup(ItemGroup group, Eventual<? extends Item> itemPromise) {
        group.list.add((Eventual<Item>) itemPromise);
    }

    public static class ItemGroup implements CreativeModeTab.DisplayItemsGenerator {
        private final List<Eventual<Item>> list = new ArrayList<>();
        private final ResourceLocation rl;

        private ItemGroup(ResourceLocation rl) {
            this.rl = rl;
        }

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
            list.forEach(i -> {
                if (i.require() instanceof DynamicCreativeItem dynamic) {
                    var array = new ArrayList<ItemStack>();
                    dynamic.fillItemCategory(array);
                    array.forEach(output::accept);
                } else {
                    output.accept(i.require());
                }
            });
        }
    }
}
