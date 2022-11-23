package io.github.foundationgames.automobility.automobile.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.BaseAttachment;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public abstract class FrontAttachment extends BaseAttachment<FrontAttachmentType<?>> {
    public FrontAttachment(FrontAttachmentType<?> type, AutomobileEntity automobile) {
        super(type, automobile);
    }

    @Override
    public Vec3 pos() {
        return this.automobile.getHeadPos();
    }

    @Override
    protected void updateTrackedAnimation(float animation) {
        this.automobile.setTrackedFrontAttachmentAnimation(animation);
    }

    public boolean canDrive(Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public void writeNbt(CompoundTag nbt) {
    }

    @Override
    public void readNbt(CompoundTag nbt) {
    }

    public void dropOrTransfer(ItemStack stack, Vec3 dropPos) {
        var rearAtt = this.automobile.getRearAttachment();
        boolean drop = true;
        if (rearAtt instanceof Container inv) {
            if (AUtils.transferInto(stack, inv)) {
                drop = false;
            }
        }
        if (drop) {
            world().addFreshEntity(new ItemEntity(world(), dropPos.x, dropPos.y, dropPos.z, stack));
        }
    }

    public static FrontAttachmentType<?> fromNbt(CompoundTag nbt) {
        return FrontAttachmentType.REGISTRY.get(ResourceLocation.tryParse(nbt.getString("type")));
    }
}
