package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.block.Sloped;
import net.minecraft.block.StairsBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StairsBlock.class)
public class StairsBlockMixin implements Sloped {

    @Override
    public boolean isSticky() {
        return false;
    }
}
