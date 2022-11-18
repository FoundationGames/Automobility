package io.github.foundationgames.automobility.forge.mixin.jsonem;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CubeDeformation.class)
public interface CubeDeformationAccess {
    @Accessor("growX")
    float automobility$radiusX();

    @Accessor("growY")
    float automobility$radiusY();

    @Accessor("growZ")
    float automobility$radiusZ();
}
