package io.github.foundationgames.automobility.forge.mixin.jsonem;

import net.minecraft.client.model.geom.builders.MaterialDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MaterialDefinition.class)
public interface MaterialDefinitionAccess {
    @Accessor("xTexSize")
    int automobility$width();

    @Accessor("yTexSize")
    int automobility$height();
}
