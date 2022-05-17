package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class RearAttachment {
    public final RearAttachmentType<?> type;
    protected final AutomobileEntity automobile;

    private float lastYaw;
    private float yaw;

    private float trackedYaw;
    private int lerpTicks;

    protected RearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        this.type = type;
        this.automobile = entity;
    }

    protected final World world() {
        return this.automobile.world;
    }

    protected final boolean isControllingSide() {
        return !world().isClient();
    }

    public final Vec3d yawVec() {
        return new Vec3d(0, 0, 1).rotateY((float) Math.toRadians(180 - this.yaw()));
    }

    public final Vec3d scaledYawVec() {
        return this.yawVec().multiply(this.type.model().pivotDistPx() * 0.0625);
    }

    public final Vec3d origin() {
        return this.automobile.getTailPos();
    }

    public final Vec3d pos() {
        return this.origin().add(this.scaledYawVec());
    }

    public float yaw() {
        return yaw;
    }

    public float yaw(float delta) {
        return MathHelper.lerpAngleDegrees(delta, this.lastYaw, this.yaw());
    }

    public void setYaw(float yaw) {
        float diff = MathHelper.wrapDegrees(yaw - this.automobile.getYaw());
        if (diff < -90 && diff > -180) yaw = this.automobile.getYaw() - 90;
        else if (diff > 90 && diff < 180) yaw = this.automobile.getYaw() + 90;

        this.yaw = yaw;
    }

    protected final void updateTrackedYaw(float yaw) {
        this.automobile.setTrackedRearAttachmentYaw(yaw);
    }

    public void onTrackedYawUpdated(float yaw) {
        this.trackedYaw = yaw;

        this.lerpTicks = this.automobile.getType().getTrackTickInterval() + 1;
    }

    public final void pull(Vec3d movement) {
        var vec = this.scaledYawVec().add(movement);
        this.setYaw(180 - (float) Math.toDegrees(Math.atan2(vec.x, vec.z)));
    }

    public void tick() {
        this.lastYaw = this.yaw();

        rotationTrackingTick();
    }

    private void rotationTrackingTick() {
        if (this.isControllingSide()) {
            this.lerpTicks = 0;
            updateTrackedYaw(yaw());
        } else if (lerpTicks > 0) {
            this.setYaw(this.yaw() + (MathHelper.wrapDegrees(this.trackedYaw - this.yaw()) / (float)this.lerpTicks));

            this.lerpTicks--;
        }
    }

    public void onRemoved() {
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

    public @Nullable NamedScreenHandlerFactory createMenu(ScreenHandlerContext ctx) {
        return null;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putFloat("yaw", this.yaw());
    }

    public void readNbt(NbtCompound nbt) {
        this.setYaw(nbt.getFloat("yaw"));
    }

    public final NbtCompound toNbt() {
        var nbt = new NbtCompound();
        nbt.putString("type", this.type.id().toString());
        this.writeNbt(nbt);
        return nbt;
    }

    public static RearAttachmentType<?> fromNbt(NbtCompound nbt) {
        return RearAttachmentType.REGISTRY.get(Identifier.tryParse(nbt.getString("type")));
    }
}
