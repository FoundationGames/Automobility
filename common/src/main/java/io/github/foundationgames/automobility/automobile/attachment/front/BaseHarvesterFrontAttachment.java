package io.github.foundationgames.automobility.automobile.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class BaseHarvesterFrontAttachment extends FrontAttachment {
    private final BlockPos.MutableBlockPos blockIter = new BlockPos.MutableBlockPos();
    private Vec3 lastPos = null;

    public BaseHarvesterFrontAttachment(FrontAttachmentType<?> type, AutomobileEntity automobile) {
        super(type, automobile);
    }

    @Override
    public void tick() {
        super.tick();
        var pos = this.pos();

        if (canModifyBlocks() && lastPos != null && lastPos.subtract(pos).length() > 0.03 && this.world() instanceof ServerLevel world) {
            this.harvest(pos, world);
        }

        this.lastPos = pos;
    }

    public void harvest(Vec3 pos, ServerLevel world) {
        int minX = (int) Math.floor(pos.x - 0.5);
        int maxX = (int) Math.floor(pos.x + 0.5);
        int minZ = (int) Math.floor(pos.z - 0.5);
        int maxZ = (int) Math.floor(pos.z + 0.5);
        int y = (int) Math.floor(pos.y + 0.25);

        Entity entity = this.automobile;
        if (this.automobile.isVehicle()) {
            entity = this.automobile.getFirstPassenger();
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                blockIter.set(x, y, z);
                var state = world.getBlockState(blockIter);
                if (canHarvest(state)) {
                    var stacks = Block.getDrops(state, world, blockIter, null, entity, ItemStack.EMPTY);
                    world.destroyBlock(blockIter, false);
                    this.onBlockHarvested(state, blockIter, stacks);
                }
            }
        }
    }

    public abstract boolean canHarvest(BlockState state);

    public abstract void onBlockHarvested(BlockState state, BlockPos pos, List<ItemStack> drops);
}
