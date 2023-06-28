package io.github.foundationgames.automobility.forge.mixin.jsonem;

import java.util.Set;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CubeDefinition.class)
public interface CubeDefinitionAccess {
    @Accessor("comment")
    String automobility$name();

    @Accessor("origin")
    Vector3f automobility$offset();

    @Accessor("dimensions")
    Vector3f automobility$dimensions();

    @Accessor("grow")
    CubeDeformation automobility$dilation();

    @Accessor("mirror")
    boolean automobility$mirror();

    @Accessor("texCoord")
    UVPair automobility$uv();

    @Accessor("texScale")
    UVPair automobility$uvScale();

    @Invoker("<init>")
    static CubeDefinition automobility$create(@Nullable String name, float textureX, float textureY, float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, CubeDeformation extra, boolean mirror, float textureScaleX, float textureScaleY, Set<Direction> p_273201_) {
        throw new AssertionError();
    }
}
