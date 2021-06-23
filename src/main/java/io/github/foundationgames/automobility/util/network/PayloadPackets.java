package io.github.foundationgames.automobility.util.network;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

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

    public static void sendSyncAutomobileToClientPacket(AutomobileEntity entity, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NbtCompound nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);
        buf.writeNbt(nbt);
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeFloat(entity.getYaw());
        buf.writeInt(entity.getId());
        ServerPlayNetworking.send(player, Automobility.id("sync_automobile"), buf);
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
                Entity e = player.world.getEntityById(entityId);
                if (e instanceof AutomobileEntity automobile) {
                    automobile.setInputs(fwd, back, left, right, space);
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(Automobility.id("sync_automobile"), (client, handler, buf, responseSender) -> {
            NbtCompound nbt = buf.readNbt();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float yaw = buf.readFloat();
            int entityId = buf.readInt();
            client.execute(() -> {
                Entity e = client.player.world.getEntityById(entityId);
                if (e instanceof AutomobileEntity automobile) {
                    automobile.readCustomDataFromNbt(nbt);
                    automobile.setPos(x, y, z);
                    automobile.setYaw(yaw);
                }
            });
        });
    }
}
