package io.github.foundationgames.automobility.automobile.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CropHarvesterFrontAttachment extends BaseHarvesterFrontAttachment {
    public CropHarvesterFrontAttachment(FrontAttachmentType<?> type, AutomobileEntity automobile) {
        super(type, automobile);
    }

    @Override
    public boolean canHarvest(BlockState state) {
        return state.getBlock() instanceof CropBlock crop && crop.isMature(state);
    }

    @Override
    public void onBlockHarvested(BlockState state, BlockPos pos, List<ItemStack> drops) {
        boolean replanted = false;
        var dropPos = this.pos();
        var world = world();
        for (var drop : drops) {
            if (!replanted && drop.getItem() instanceof BlockItem item) {
                var newState = item.getBlock().getDefaultState();
                if (newState.canPlaceAt(world, pos)) {
                    world.setBlockState(pos, newState);
                    drop.decrement(1);
                    replanted = true;
                }
            }
            if (!drop.isEmpty()) {
                this.dropOrTransfer(drop, dropPos);
            }
        }
    }
}
