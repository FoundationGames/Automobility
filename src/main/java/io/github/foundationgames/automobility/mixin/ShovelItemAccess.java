package io.github.foundationgames.automobility.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ShovelItem.class)
public interface ShovelItemAccess {
    @Accessor("PATH_STATES")
    static Map<Block, BlockState> getPathStates() {
        throw new AssertionError();
    }
}
