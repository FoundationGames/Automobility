package io.github.foundationgames.automobility.block;

import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public interface OffroadBlock {
    float getSpeedMultiplier(BlockState blockState);
    Vector3f getDebrisColor(BlockState blockState);
}
