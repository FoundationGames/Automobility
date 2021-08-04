package io.github.foundationgames.automobility.util.network;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public enum PayloadPackets {;
    @Environment(EnvType.CLIENT)
    public static void sendSyncAutomobileInputPacket(AutomobileEntity entity, boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(fwd);
        buf.writeBoolean(back);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(space);
        buf.writeInt(entity.getId());
        ClientPlayNetworking.send(Automobility.id("sync_automobile_inputs"), buf);
    }

    @Environment(EnvType.CLIENT)
    public static void requestSyncAutomobileComponentsPacket(AutomobileEntity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        ClientPlayNetworking.send(Automobility.id("request_sync_automobile_components"), buf);
    }

    public static void sendSyncAutomobileDataPacket(AutomobileEntity entity, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        entity.writeSyncToClientData(buf);
        ServerPlayNetworking.send(player, Automobility.id("sync_automobile_data"), buf);
    }

    public static void sendSyncAutomobilePosPacket(AutomobileEntity entity, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeFloat(entity.getYaw());
        ServerPlayNetworking.send(player, Automobility.id("sync_automobile_position"), buf);
    }

    public static void sendSyncAutomobileComponentsPacket(AutomobileEntity entity, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeString(entity.getFrame().id().toString());
        buf.writeString(entity.getWheels().id().toString());
        buf.writeString(entity.getEngine().id().toString());
        ServerPlayNetworking.send(player, Automobility.id("sync_automobile_components"), buf);
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(Automobility.id("sync_automobile_inputs"), (server, player, handler, buf, responseSender) -> {
            boolean fwd = buf.readBoolean();
            boolean back = buf.readBoolean();
            boolean left = buf.readBoolean();
            boolean right = buf.readBoolean();
            boolean space = buf.readBoolean();
            int entityId = buf.readInt();
            server.execute(() -> {
                if (player.world.getEntityById(entityId) instanceof AutomobileEntity automobile) {
                    automobile.setInputs(fwd, back, left, right, space);
                    automobile.markDirty();
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(Automobility.id("request_sync_automobile_components"), (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            server.execute(() -> {
                if (player.world.getEntityById(entityId) instanceof AutomobileEntity automobile) {
                    sendSyncAutomobileComponentsPacket(automobile, player);
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(Automobility.id("sync_automobile_data"), (client, handler, buf, responseSender) -> {
            PacketByteBuf dup = PacketByteBufs.copy(buf);
            int entityId = dup.readInt();
            client.execute(() -> {
                if (client.player.world.getEntityById(entityId) instanceof AutomobileEntity automobile) {
                    automobile.readSyncToClientData(dup);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Automobility.id("sync_automobile_position"), (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float yaw = buf.readFloat();
            client.execute(() -> {
                if (client.player.world.getEntityById(entityId) instanceof AutomobileEntity automobile) {
                    automobile.updatePositionAndAngles(x, y, z, yaw, 0);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Automobility.id("sync_automobile_components"), (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            var frame = AutomobileFrame.REGISTRY.getOrDefault(Identifier.tryParse(buf.readString()));
            var wheel = AutomobileWheel.REGISTRY.getOrDefault(Identifier.tryParse(buf.readString()));
            var engine = AutomobileEngine.REGISTRY.getOrDefault(Identifier.tryParse(buf.readString()));
            client.execute(() -> {
                if (client.player.world.getEntityById(entityId) instanceof AutomobileEntity automobile) {
                    automobile.setComponents(frame, wheel, engine);
                }
            });
        });
    }
}
