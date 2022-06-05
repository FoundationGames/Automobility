package io.github.foundationgames.automobility.automobile.attachment;

import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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

    protected final World world() {
        return this.automobile.world;
    }

    public abstract Vec3d pos();

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

    public abstract void writeNbt(NbtCompound nbt);

    public abstract void readNbt(NbtCompound nbt);

    protected boolean canModifyBlocks() {
        if (this.automobile.getFirstPassenger() instanceof PlayerEntity player && player.canModifyBlocks()) {
            return true;
        }

        for (int i = 0; i < 4; i++) {
            if (world().getBlockState(this.automobile.getBlockPos().down(i)).isOf(AutomobilityBlocks.ALLOW)) {
                return true;
            }
        }
        return false;
    }

    public final NbtCompound toNbt() {
        var nbt = new NbtCompound();
        nbt.putString("type", this.type.getId().toString());
        this.writeNbt(nbt);
        return nbt;
    }
}
