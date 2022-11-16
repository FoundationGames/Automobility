package io.github.foundationgames.automobility.util.network;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.rear.BannerPostRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.ExtendableRearAttachment;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public enum PayloadPackets {;
    @Environment(EnvType.CLIENT)
    public static void sendSyncAutomobileInputPacket(AutomobileEntity entity, boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(fwd);
        buf.writeBoolean(back);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(space);
        buf.writeInt(entity.getId());
        ClientPlayNetworking.send(Automobility.rl("sync_automobile_inputs"), buf);
    }

    @Environment(EnvType.CLIENT)
    public static void requestSyncAutomobileComponentsPacket(AutomobileEntity entity) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        ClientPlayNetworking.send(Automobility.rl("request_sync_automobile_components"), buf);
    }

    public static void sendSyncAutomobileDataPacket(AutomobileEntity entity, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        entity.writeSyncToClientData(buf);
        ServerPlayNetworking.send(player, Automobility.rl("sync_automobile_data"), buf);
    }

    public static void sendSyncAutomobileComponentsPacket(AutomobileEntity entity, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeUtf(entity.getFrame().id().toString());
        buf.writeUtf(entity.getWheels().id().toString());
        buf.writeUtf(entity.getEngine().id().toString());
        ServerPlayNetworking.send(player, Automobility.rl("sync_automobile_components"), buf);
    }

    public static void sendSyncAutomobileAttachmentsPacket(AutomobileEntity entity, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeUtf(entity.getRearAttachmentType().id().toString());
        buf.writeUtf(entity.getFrontAttachmentType().id().toString());
        ServerPlayNetworking.send(player, Automobility.rl("sync_automobile_attachments"), buf);
    }

    public static void sendBannerPostAttachmentUpdatePacket(AutomobileEntity entity, CompoundTag banner, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());

        if (entity.getRearAttachment() instanceof BannerPostRearAttachment) {
            buf.writeInt(entity.getId());
            buf.writeNbt(banner);
            ServerPlayNetworking.send(player, Automobility.rl("update_banner_post"), buf);
        }
    }

    public static void sendExtendableAttachmentUpdatePacket(AutomobileEntity entity, boolean extended, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());

        if (entity.getRearAttachment() instanceof ExtendableRearAttachment) {
            buf.writeInt(entity.getId());
            buf.writeBoolean(extended);
            ServerPlayNetworking.send(player, Automobility.rl("update_extendable_attachment"), buf);
        }
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(Automobility.rl("sync_automobile_inputs"), (server, player, handler, buf, responseSender) -> {
            boolean fwd = buf.readBoolean();
            boolean back = buf.readBoolean();
            boolean left = buf.readBoolean();
            boolean right = buf.readBoolean();
            boolean space = buf.readBoolean();
            int entityId = buf.readInt();
            server.execute(() -> {
                if (player.level.getEntity(entityId) instanceof AutomobileEntity automobile) {
                    automobile.setInputs(fwd, back, left, right, space);
                    automobile.markDirty();
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(Automobility.rl("request_sync_automobile_components"), (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            server.execute(() -> {
                if (player.level.getEntity(entityId) instanceof AutomobileEntity automobile) {
                    sendSyncAutomobileComponentsPacket(automobile, player);
                    sendSyncAutomobileAttachmentsPacket(automobile, player);

                    var fAtt = automobile.getFrontAttachment();
                    if (fAtt != null) fAtt.updatePacketRequested(player);

                    var rAtt = automobile.getRearAttachment();
                    if (rAtt != null) rAtt.updatePacketRequested(player);
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(Automobility.rl("sync_automobile_data"), (client, handler, buf, responseSender) -> {
            FriendlyByteBuf dup = PacketByteBufs.copy(buf);
            int entityId = dup.readInt();
            client.execute(() -> {
                if (client.player.level.getEntity(entityId) instanceof AutomobileEntity automobile) {
                    automobile.readSyncToClientData(dup);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Automobility.rl("sync_automobile_components"), (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            var frame = AutomobileFrame.REGISTRY.getOrDefault(ResourceLocation.tryParse(buf.readUtf()));
            var wheel = AutomobileWheel.REGISTRY.getOrDefault(ResourceLocation.tryParse(buf.readUtf()));
            var engine = AutomobileEngine.REGISTRY.getOrDefault(ResourceLocation.tryParse(buf.readUtf()));
            client.execute(() -> {
                if (client.player.level.getEntity(entityId) instanceof AutomobileEntity automobile) {
                    automobile.setComponents(frame, wheel, engine);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Automobility.rl("sync_automobile_attachments"), (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            var rearAtt = RearAttachmentType.REGISTRY.getOrDefault(ResourceLocation.tryParse(buf.readUtf()));
            var frontAtt = FrontAttachmentType.REGISTRY.getOrDefault(ResourceLocation.tryParse(buf.readUtf()));
            client.execute(() -> {
                if (client.player.level.getEntity(entityId) instanceof AutomobileEntity automobile) {
                    automobile.setRearAttachment(rearAtt);
                    automobile.setFrontAttachment(frontAtt);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Automobility.rl("update_banner_post"), (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            var banner = buf.readNbt();
            client.execute(() -> {
                if (client.player.level.getEntity(entityId) instanceof AutomobileEntity automobile &&
                        automobile.getRearAttachment() instanceof BannerPostRearAttachment bannerPost) {
                    bannerPost.setFromNbt(banner);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Automobility.rl("update_extendable_attachment"), (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            boolean extended = buf.readBoolean();
            client.execute(() -> {
                if (client.player.level.getEntity(entityId) instanceof AutomobileEntity automobile &&
                        automobile.getRearAttachment() instanceof ExtendableRearAttachment att) {
                    att.setExtended(extended);
                }
            });
        });
    }
}
