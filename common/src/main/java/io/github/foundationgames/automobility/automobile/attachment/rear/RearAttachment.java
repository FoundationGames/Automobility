package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.BaseAttachment;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class RearAttachment extends BaseAttachment<RearAttachmentType<?>> {
    private float lastYaw;
    private float yaw;

    private float trackedYaw;
    private int yawLerpProgress;

    protected RearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    public final Vec3 yawVec() {
        return new Vec3(0, 0, 1).yRot((float) Math.toRadians(180 - this.yaw()));
    }

    public final Vec3 scaledYawVec() {
        return this.yawVec().scale(this.type.model().pivotDistPx() * 0.0625);
    }

    public final Vec3 origin() {
        return this.automobile.getTailPos();
    }

    @Override
    public final Vec3 pos() {
        return this.origin().add(this.scaledYawVec());
    }

    public float yaw() {
        return yaw;
    }

    public float yaw(float delta) {
        return Mth.rotLerp(delta, this.lastYaw, this.yaw());
    }

    public void setYaw(float yaw) {
        float diff = Mth.wrapDegrees(yaw - this.automobile.getYRot());
        if (diff < -90 && diff > -180) yaw = this.automobile.getYRot() - 90;
        else if (diff > 90 && diff < 180) yaw = this.automobile.getYRot() + 90;

        this.yaw = yaw;
    }

    protected final void updateTrackedYaw(float yaw) {
        this.automobile.setTrackedRearAttachmentYaw(yaw);
    }

    public void onTrackedYawUpdated(float yaw) {
        this.trackedYaw = yaw;

        this.yawLerpProgress = this.automobile.getType().updateInterval() + 1;
    }

    protected final void updateTrackedAnimation(float animation) {
        this.automobile.setTrackedRearAttachmentAnimation(animation);
    }

    public final void pull(Vec3 movement) {
        var vec = this.scaledYawVec().add(movement);
        this.setYaw(180 - (float) Math.toDegrees(Math.atan2(vec.x, vec.z)));
    }

    public void tick() {
        this.lastYaw = this.yaw();

        rotationTrackingTick();
    }

    private void rotationTrackingTick() {
        if (!world().isClientSide()) {
            this.yawLerpProgress = 0;
            updateTrackedYaw(yaw());
        } else if (yawLerpProgress > 0) {
            this.setYaw(this.yaw() + (Mth.wrapDegrees(this.trackedYaw - this.yaw()) / (float)this.yawLerpProgress));

            this.yawLerpProgress--;
        }
    }

    public boolean isRideable() {
        return false;
    }

    public double getPassengerHeightOffset() {
        return 0.5;
    }

    public boolean hasMenu() {
        return false;
    }

    public @Nullable MenuProvider createMenu(ContainerLevelAccess ctx) {
        return null;
    }

    public void writeNbt(CompoundTag nbt) {
        nbt.putFloat("yaw", this.yaw());
    }

    public void readNbt(CompoundTag nbt) {
        this.setYaw(nbt.getFloat("yaw"));
    }

    public static RearAttachmentType<?> fromNbt(CompoundTag nbt) {
        return RearAttachmentType.REGISTRY.get(ResourceLocation.tryParse(nbt.getString("type")));
    }
}
