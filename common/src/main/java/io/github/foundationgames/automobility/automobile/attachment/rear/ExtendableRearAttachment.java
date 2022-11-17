package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.network.CommonPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public abstract class ExtendableRearAttachment extends DeployableRearAttachment {
    protected boolean extended;

    private int extendAnimation = 0;
    private int lastExtendAnimation = extendAnimation;

    protected ExtendableRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    public float extendAnimation(float delta) {
        return Mth.lerp(delta, lastExtendAnimation, extendAnimation) / 14;
    }

    public void setExtended(boolean extended) {
        if (!this.world().isClientSide()) {
            this.updateTrackedAnimation(extended ? 1f : 0f);
        }

        this.extended = extended;
    }

    public boolean extended() {
        return this.extended;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.world().isClientSide()) {
            this.lastExtendAnimation = this.extendAnimation;
            this.extendAnimation = AUtils.shift(this.extendAnimation, 1, this.extended() ? 0 : this.extendAnimTime());
        }
    }

    @Override
    public void updatePacketRequested(ServerPlayer player) {
        super.updatePacketRequested(player);

        CommonPackets.sendExtendableAttachmentUpdatePacket(this.automobile(), this.extended(), player);
    }

    @Override
    public void writeNbt(CompoundTag nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("extended", this.extended());
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);

        this.setExtended(nbt.getBoolean("extended"));
    }

    @Override
    public void deploy() {
        this.setExtended(!this.extended());
    }

    @Override
    public void onTrackedAnimationUpdated(float animation) {
        super.onTrackedAnimationUpdated(animation);

        this.setExtended(animation > 0);
    }

    protected abstract int extendAnimTime();
}
