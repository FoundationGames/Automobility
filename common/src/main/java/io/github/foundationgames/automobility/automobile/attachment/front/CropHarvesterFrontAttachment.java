package io.github.foundationgames.automobility.automobile.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CropHarvesterFrontAttachment extends BaseHarvesterFrontAttachment {
    public CropHarvesterFrontAttachment(FrontAttachmentType<?> type, AutomobileEntity automobile) {
        super(type, automobile);
    }

    @Override
    public boolean canHarvest(BlockState state) {
        return state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
    }

    @Override
    public void onBlockHarvested(BlockState state, BlockPos pos, List<ItemStack> drops) {
        boolean replanted = false;
        var dropPos = this.pos();
        var world = world();
        for (var drop : drops) {
            if (!replanted && drop.getItem() instanceof BlockItem item) {
                var newState = item.getBlock().defaultBlockState();
                if (newState.canSurvive(world, pos)) {
                    world.setBlockAndUpdate(pos, newState);
                    drop.shrink(1);
                    replanted = true;
                }
            }
            if (!drop.isEmpty()) {
                this.dropOrTransfer(drop, dropPos);
            }
        }
    }
}
