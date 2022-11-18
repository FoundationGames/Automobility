package io.github.foundationgames.automobility.forge.mixin.jsonem;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LayerDefinition.class)
public interface LayerDefinitionAccess {
    @Accessor("mesh")
    MeshDefinition automobility$root();

    @Accessor("material")
    MaterialDefinition automobility$texture();

    @Invoker("<init>")
    static LayerDefinition automobility$create(MeshDefinition data, MaterialDefinition dimensions) {
        throw new AssertionError();
    }
}
