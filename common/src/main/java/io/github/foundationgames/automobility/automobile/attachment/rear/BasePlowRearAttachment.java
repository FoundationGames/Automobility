package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class BasePlowRearAttachment extends ExtendableRearAttachment {
    private final BlockPos.MutableBlockPos blockIter = new BlockPos.MutableBlockPos();
    private Vec3 lastPos = null;

    public BasePlowRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
        this.setExtended(true);
    }

    @Override
    public void tick() {
        super.tick();
        var pos = this.origin().add(this.yawVec().scale(0.11 * this.type.model().pivotDistPx()));

        if (this.extended() && canModifyBlocks() && lastPos != null && lastPos.subtract(pos).length() > 0.03 && this.world() instanceof ServerLevel world) {
            this.plow(pos, world);
        }

        this.lastPos = pos;
    }

    public void plow(Vec3 pos, ServerLevel world) {
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
                    world.setBlockAndUpdate(blockIter, result);
                    playSound = true;
                }
            }
        }

        if (playSound) {
            world.playSound(null, pos.x, pos.y, pos.z, this.plowSound(), SoundSource.BLOCKS, 0.8f, 1f);
        }
    }

    @Override
    public void setExtended(boolean deployed) {
        if (!world().isClientSide() && deployed != this.extended()) {
            var pos = this.pos();
            world().playSound(null, pos.x, pos.y, pos.z, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 0.6f, 1.4f);
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
