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
import io.github.foundationgames.automobility.intermediary.Intermediary;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ClientPackets {;
    public static void sendSyncAutomobileInputPacket(AutomobileEntity entity, boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(fwd);
        buf.writeBoolean(back);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(space);
        buf.writeInt(entity.getId());
        Intermediary.get().clientSendPacket(Automobility.rl("sync_automobile_inputs"), buf);
    }

    public static void requestSyncAutomobileComponentsPacket(AutomobileEntity entity) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        Intermediary.get().clientSendPacket(Automobility.rl("request_sync_automobile_components"), buf);
    }

    public static void initClient() {
        Intermediary.get().clientReceivePacket(Automobility.rl("sync_automobile_data"), (client, buf) -> {
            FriendlyByteBuf dup = new FriendlyByteBuf(buf.copy());
            int entityId = dup.readInt();
            client.execute(() -> {
                if (client.player.level.getEntity(entityId) instanceof AutomobileEntity automobile) {
                    automobile.readSyncToClientData(dup);
                }
            });
        });
        Intermediary.get().clientReceivePacket(Automobility.rl("sync_automobile_components"), (client, buf) -> {
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
        Intermediary.get().clientReceivePacket(Automobility.rl("sync_automobile_attachments"), (client, buf) -> {
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
        Intermediary.get().clientReceivePacket(Automobility.rl("update_banner_post"), (client, buf) -> {
            int entityId = buf.readInt();
            var banner = buf.readNbt();
            client.execute(() -> {
                if (client.player.level.getEntity(entityId) instanceof AutomobileEntity automobile &&
                        automobile.getRearAttachment() instanceof BannerPostRearAttachment bannerPost) {
                    bannerPost.setFromNbt(banner);
                }
            });
        });
        Intermediary.get().clientReceivePacket(Automobility.rl("update_extendable_attachment"), (client, buf) -> {
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
