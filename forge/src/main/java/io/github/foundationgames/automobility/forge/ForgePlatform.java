package io.github.foundationgames.automobility.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.foundationgames.automobility.controller.AutomobileController;
import io.github.foundationgames.automobility.forge.client.BEWLRs;
import io.github.foundationgames.automobility.forge.mixin.BlockColorsAccess;
import io.github.foundationgames.automobility.forge.network.AutomobilityPacketHandler;
import io.github.foundationgames.automobility.forge.vendored.jsonem.JsonEM;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.HexCons;
import io.github.foundationgames.automobility.util.TriCons;
import io.github.foundationgames.automobility.util.TriFunc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
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
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgePlatform implements Platform {
    private static final ForgePlatform INSTANCE = new ForgePlatform();

    public static void init() {
        Platform.init(INSTANCE);
    }

    @Override
    public CreativeModeTab creativeTab(ResourceLocation rl, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        return CreativeModeTab.builder()
                .icon(icon)
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .title(Component.translatable("itemGroup." + rl.getNamespace() + "." + rl.getPath()))
                .displayItems(displayItemsGenerator)
                .build();
    }

    @Override
    public void builtinItemRenderer(Item item, HexCons<ItemStack, ItemDisplayContext, PoseStack, MultiBufferSource, Integer, Integer> renderer) {
        BEWLRs.add(item, renderer);
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
        return ((BlockColorsAccess)Minecraft.getInstance().getBlockColors()).automobility$getForgeColorMap()
                .get(ForgeRegistries.BLOCKS.getDelegateOrThrow(state.getBlock()));
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntity(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::apply, blocks).build(null);
    }

    @Override
    public <T extends BlockEntity> void blockEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<T>> provider) {
        BlockEntityRenderers.register(type, provider::apply);
    }

    @Override
    public void serverSendPacket(ServerPlayer player, ResourceLocation rl, FriendlyByteBuf buf) {
        AutomobilityPacketHandler.serverToClient(player, rl, buf);
    }

    @Override
    public void clientSendPacket(ResourceLocation rl, FriendlyByteBuf buf) {
        AutomobilityPacketHandler.clientToServer(rl, buf);
    }

    @Override
    public void serverReceivePacket(ResourceLocation rl, TriCons<MinecraftServer, ServerPlayer, FriendlyByteBuf> run) {
        AutomobilityPacketHandler.addServerReceiver(rl, run);
    }

    @Override
    public void clientReceivePacket(ResourceLocation rl, BiConsumer<Minecraft, FriendlyByteBuf> run) {
        AutomobilityPacketHandler.addClientReceiver(rl, run);
    }

    @Override
    public <T extends Entity> EntityType<T> entityType(MobCategory category, BiFunction<EntityType<?>, Level, T> factory, EntityDimensions size, int updateRate, int updateRange) {
        return EntityType.Builder.of(factory::apply, category).sized(size.width, size.height).updateInterval(updateRate).clientTrackingRange(updateRange).build("");
    }

    @Override
    public <T extends Entity> void entityRenderer(EntityType<T> entity, Function<EntityRendererProvider.Context, EntityRenderer<T>> factory) {
        EntityRenderers.register(entity, factory::apply);
    }

    @Override
    public void modelLayer(ModelLayerLocation layer) {
        JsonEM.registerModelLayer(layer);
    }

    @Override
    public SimpleParticleType simpleParticleType(boolean z) {
        return new SimpleParticleType(z);
    }

    @Override
    public AutomobileController controller() {
        return AutomobileController.INCOMPATIBLE;
    }

}
