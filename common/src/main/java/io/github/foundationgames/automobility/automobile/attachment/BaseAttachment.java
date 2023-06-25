package io.github.foundationgames.automobility.automobile.attachment;

import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class BaseAttachment<T extends AutomobileComponent<T>> {
    public final T type;
    protected final AutomobileEntity automobile;

    private float animation;

    public BaseAttachment(T type, AutomobileEntity automobile) {
        this.type = type;
        this.automobile = automobile;
    }

    public final AutomobileEntity automobile() {
        return this.automobile;
    }

    protected final Level world() {
        return this.automobile.level();
    }

    public abstract Vec3 pos();

    public float animation() {
        return animation;
    }

    public void setAnimation(float animation) {
        this.animation = animation;
    }

    protected abstract void updateTrackedAnimation(float animation);

    public void onTrackedAnimationUpdated(float animation) {
        this.setAnimation(animation);
    }

    public void tick() {
    }

    public void onRemoved() {
    }

    public abstract void writeNbt(CompoundTag nbt);

    public abstract void readNbt(CompoundTag nbt);

    public void updatePacketRequested(ServerPlayer player) {
    }

    protected boolean canModifyBlocks() {
        if (this.automobile.getFirstPassenger() instanceof Player player && player.mayBuild()) {
            return true;
        }

        for (int i = 0; i < 4; i++) {
            if (world().getBlockState(this.automobile.blockPosition().below(i)).is(AutomobilityBlocks.ALLOW.require())) {
                return true;
            }
        }
        return false;
    }

    public final CompoundTag toNbt() {
        var nbt = new CompoundTag();
        nbt.putString("type", this.type.getId().toString());
        this.writeNbt(nbt);
        return nbt;
    }
}
