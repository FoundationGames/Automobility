package io.github.foundationgames.automobility.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.Properties.class)
public interface BlockBehaviorAccess {
    @Accessor
    boolean getSpawnTerrainParticles();

    @Accessor
    float getExplosionResistance();
}
