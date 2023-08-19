package io.github.foundationgames.automobility.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.foundationgames.automobility.controller.AutomobileController;
import io.github.foundationgames.automobility.fabric.controller.controlify.ControlifyController;
import io.github.foundationgames.automobility.fabric.controller.midnightcontrols.MidnightController;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.HexCons;
import io.github.foundationgames.automobility.util.TriCons;
import io.github.foundationgames.automobility.util.TriFunc;
import io.github.foundationgames.jsonem.JsonEM;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricPlatform implements Platform {
    private static final FabricPlatform INSTANCE = new FabricPlatform();

    private AutomobileController automobileController = null;

    public static void init() {
        Platform.init(INSTANCE);
    }

    @Override
    public CreativeModeTab creativeTab(ResourceLocation rl, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        return FabricItemGroup.builder()
                .icon(icon)
                .title(Component.translatable("itemGroup." + rl.getNamespace() + "." + rl.getPath()))
                .displayItems(displayItemsGenerator)
                .build();
    }

    @Override
    public void builtinItemRenderer(Item item, HexCons<ItemStack, ItemDisplayContext, PoseStack, MultiBufferSource, Integer, Integer> renderer) {
        BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::accept);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> menuType(BiFunction<Integer, Inventory, T> factory) {
        return new MenuType<>(factory::apply, FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(MenuType<T> type, TriFunc<T, Inventory, Component, U> factory) {
        MenuScreens.register(type, factory::apply);
    }

    @Override
    public @Nullable BlockColor blockColor(BlockState state) {
        return ColorProviderRegistry.BLOCK.get(state.getBlock());
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntity(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory::apply, blocks).build();
    }

    @Override
    public <T extends BlockEntity> void blockEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<T>> provider) {
        BlockEntityRendererRegistry.register(type, provider::apply);
    }

    @Override
    public void serverSendPacket(ServerPlayer player, ResourceLocation rl, FriendlyByteBuf buf) {
        ServerPlayNetworking.send(player, rl, buf);
    }

    @Override
    public void clientSendPacket(ResourceLocation rl, FriendlyByteBuf buf) {
        ClientPlayNetworking.send(rl, buf);
    }

    @Override
    public void serverReceivePacket(ResourceLocation rl, TriCons<MinecraftServer, ServerPlayer, FriendlyByteBuf> run) {
        ServerPlayNetworking.registerGlobalReceiver(rl, (server, player, handler, buf, responseSender) ->
                run.accept(server, player, buf));
    }

    @Override
    public void clientReceivePacket(ResourceLocation rl, BiConsumer<Minecraft, FriendlyByteBuf> run) {
        ClientPlayNetworking.registerGlobalReceiver(rl, (client, handler, buf, responseSender) ->
                run.accept(client, buf));
    }

    @Override
    public <T extends Entity> EntityType<T> entityType(MobCategory category, BiFunction<EntityType<?>, Level, T> factory, EntityDimensions size, int updateRate, int updateRange) {
        return FabricEntityTypeBuilder.create(category, factory::apply).dimensions(size).trackedUpdateRate(updateRate).trackRangeChunks(updateRange).build();
    }

    @Override
    public <T extends Entity> void entityRenderer(EntityType<T> entity, Function<EntityRendererProvider.Context, EntityRenderer<T>> factory) {
        EntityRendererRegistry.register(entity, factory::apply);
    }

    @Override
    public void modelLayer(ModelLayerLocation layer) {
        JsonEM.registerModelLayer(layer);
    }

    @Override
    public SimpleParticleType simpleParticleType(boolean z) {
        return FabricParticleTypes.simple(z);
    }

    @Override
    public AutomobileController controller() {
        if (automobileController == null) {
            if (FabricLoader.getInstance().isModLoaded("controlify")) {
                automobileController = new ControlifyController();
            } else if (FabricLoader.getInstance().isModLoaded("midnightcontrols")) {
                automobileController = new MidnightController();
            } else {
                automobileController = AutomobileController.INCOMPATIBLE;
            }
        }

        return automobileController;
    }

}
