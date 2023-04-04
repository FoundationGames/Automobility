package io.github.foundationgames.automobility.forge.mixin.jsonem;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(PartDefinition.class)
public interface PartDefinitionAccess {
    @Accessor("cubes")
    List<CubeDefinition> automobility$cuboids();

    @Accessor("partPose")
    PartPose automobility$transform();

    @Accessor("children")
    Map<String, PartDefinition> automobility$children();

    @Invoker("<init>")
    static PartDefinition automobility$create(List<CubeDefinition> cuboids, PartPose rotation) {
        throw new AssertionError();
    }
}
