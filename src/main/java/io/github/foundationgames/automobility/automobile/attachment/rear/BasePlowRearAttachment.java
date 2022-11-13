package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class BasePlowRearAttachment extends ExtendableRearAttachment {
    private final BlockPos.Mutable blockIter = new BlockPos.Mutable();
    private Vec3d lastPos = null;

    public BasePlowRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
        this.setExtended(true);
    }

    @Override
    public void tick() {
        super.tick();
        var pos = this.origin().add(this.yawVec().multiply(0.11 * this.type.model().pivotDistPx()));

        if (this.extended() && canModifyBlocks() && lastPos != null && lastPos.subtract(pos).length() > 0.03 && this.world() instanceof ServerWorld world) {
            this.plow(pos, world);
        }

        this.lastPos = pos;
    }

    public void plow(Vec3d pos, ServerWorld world) {
        int minX = (int) Math.floor(pos.x - 0.5);
        int maxX = (int) Math.floor(pos.x + 0.5);
        int minZ = (int) Math.floor(pos.z - 0.5);
        int maxZ = (int) Math.floor(pos.z + 0.5);
        int y = (int) Math.floor(pos.y + this.searchHeight());

        boolean playSound = false;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                blockIter.set(x, y, z);
                var state = world.getBlockState(blockIter);
                var result = this.plowResult(blockIter, state);

                if (result != state) {
                    world.setBlockState(blockIter, result);
                    playSound = true;
                }
            }
        }

        if (playSound) {
            world.playSound(null, pos.x, pos.y, pos.z, this.plowSound(), SoundCategory.BLOCKS, 0.8f, 1f);
        }
    }

    @Override
    public void setExtended(boolean deployed) {
        if (!world().isClient() && deployed != this.extended()) {
            var pos = this.pos();
            world().playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 0.6f, 1.4f);
        }

        super.setExtended(deployed);
    }

    public abstract SoundEvent plowSound();

    public abstract double searchHeight();

    public abstract BlockState plowResult(BlockPos pos, BlockState state);

    @Override
    protected int extendAnimTime() {
        return 3;
    }
}
