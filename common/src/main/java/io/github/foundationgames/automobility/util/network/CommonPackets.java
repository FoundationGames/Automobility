package io.github.foundationgames.automobility.util.network;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.attachment.rear.BannerPostRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.ExtendableRearAttachment;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.platform.Platform;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public enum CommonPackets {;
    public static void sendSyncAutomobileDataPacket(AutomobileEntity entity, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        entity.writeSyncToClientData(buf);
        Platform.get().serverSendPacket(player, Automobility.rl("sync_automobile_data"), buf);
    }

    public static void sendSyncAutomobileComponentsPacket(AutomobileEntity entity, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeUtf(entity.getFrame().id().toString());
        buf.writeUtf(entity.getWheels().id().toString());
        buf.writeUtf(entity.getEngine().id().toString());
        Platform.get().serverSendPacket(player, Automobility.rl("sync_automobile_components"), buf);
    }

    public static void sendSyncAutomobileAttachmentsPacket(AutomobileEntity entity, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeUtf(entity.getRearAttachmentType().id().toString());
        buf.writeUtf(entity.getFrontAttachmentType().id().toString());
        Platform.get().serverSendPacket(player, Automobility.rl("sync_automobile_attachments"), buf);
    }

    public static void sendBannerPostAttachmentUpdatePacket(AutomobileEntity entity, CompoundTag banner, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());

        if (entity.getRearAttachment() instanceof BannerPostRearAttachment) {
            buf.writeInt(entity.getId());
            buf.writeNbt(banner);
            Platform.get().serverSendPacket(player, Automobility.rl("update_banner_post"), buf);
        }
    }

    public static void sendExtendableAttachmentUpdatePacket(AutomobileEntity entity, boolean extended, ServerPlayer player) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());

        if (entity.getRearAttachment() instanceof ExtendableRearAttachment) {
            buf.writeInt(entity.getId());
            buf.writeBoolean(extended);
            Platform.get().serverSendPacket(player, Automobility.rl("update_extendable_attachment"), buf);
        }
    }

    public static void init() {
        Platform.get().serverReceivePacket(Automobility.rl("sync_automobile_inputs"), (server, player, buf) -> {
            boolean fwd = buf.readBoolean();
            boolean back = buf.readBoolean();
            boolean left = buf.readBoolean();
            boolean right = buf.readBoolean();
            boolean space = buf.readBoolean();
            int entityId = buf.readInt();
            server.execute(() -> {
                if (player.level().getEntity(entityId) instanceof AutomobileEntity automobile) {
                    automobile.setInputs(fwd, back, left, right, space);
                    automobile.markDirty();
                }
            });
        });
        Platform.get().serverReceivePacket(Automobility.rl("request_sync_automobile_components"), (server, player, buf) -> {
            int entityId = buf.readInt();
            server.execute(() -> {
                if (player.level().getEntity(entityId) instanceof AutomobileEntity automobile) {
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
}
