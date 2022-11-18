package io.github.foundationgames.automobility.forge.network;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.TriCons;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AutomobilityPacketHandler {
    private static final String PROTOCOL_VERSION = "0.3";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            Automobility.rl("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final Map<ResourceLocation, BiConsumer<Minecraft, FriendlyByteBuf>> CLIENT_RECEIVERS = new HashMap<>();
    private static final Map<ResourceLocation, TriCons<MinecraftServer, ServerPlayer, FriendlyByteBuf>> SERVER_RECEIVERS = new HashMap<>();

    public static void addClientReceiver(ResourceLocation rl, BiConsumer<Minecraft, FriendlyByteBuf> receiver) {
        CLIENT_RECEIVERS.put(rl, receiver);
    }

    public static void addServerReceiver(ResourceLocation rl, TriCons<MinecraftServer, ServerPlayer, FriendlyByteBuf> receiver) {
        SERVER_RECEIVERS.put(rl, receiver);
    }

    public static void clientToServer(ResourceLocation rl, FriendlyByteBuf buf) {
        INSTANCE.sendToServer(new PayloadMessage(rl, buf));
    }

    public static void serverToClient(ServerPlayer player, ResourceLocation rl, FriendlyByteBuf buf) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PayloadMessage(rl, buf));
    }

    public static void init() {
        INSTANCE.registerMessage(0, PayloadMessage.class, PayloadMessage::encode, PayloadMessage::decode, PayloadMessage::handle);
    }

    public static class PayloadMessage {
        private final ResourceLocation id;
        private final FriendlyByteBuf payload;

        public PayloadMessage(ResourceLocation id, FriendlyByteBuf payload) {
            this.id = id;
            this.payload = payload;
        }

        public static PayloadMessage decode(FriendlyByteBuf buf) {
            return new PayloadMessage(buf.readResourceLocation(), new FriendlyByteBuf(buf.readBytes(buf.readableBytes())));
        }

        private void encode(FriendlyByteBuf buf) {
            buf.writeResourceLocation(this.id);
            buf.writeBytes(this.payload);
        }

        private void handle(Supplier<NetworkEvent.Context> ctxProvider) {
            var ctx = ctxProvider.get();

            if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                CLIENT_RECEIVERS.get(this.id).accept(Minecraft.getInstance(), this.payload);
            } else if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER) {
                var player = ctx.getSender();
                if (player != null) {
                    SERVER_RECEIVERS.get(this.id).accept(player.server, player, this.payload);
                }
            }

            ctx.setPacketHandled(true);
        }
    }
}
