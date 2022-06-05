package io.github.foundationgames.automobility.automobile.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class GrassCutterFrontAttachment extends BaseHarvesterFrontAttachment {
    public GrassCutterFrontAttachment(FrontAttachmentType<?> type, AutomobileEntity automobile) {
        super(type, automobile);
    }

    @Override
    public boolean canHarvest(BlockState state) {
        return (state.getBlock() instanceof PlantBlock) && !(state.getBlock() instanceof CropBlock);
    }

    @Override
    public void onBlockHarvested(BlockState state, BlockPos pos, List<ItemStack> drops) {
        var dropPos = this.pos();
        for (var drop : drops) {
            this.dropOrTransfer(drop, dropPos);
        }
    }
}
