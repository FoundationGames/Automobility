package io.github.foundationgames.automobility.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface GeometryBuilder {
    GeometryBuilder vertex(float x, float y, float z, @Nullable Direction face, float nx, float ny, float nz,
                           TextureAtlasSprite sprite, float u, float v);

    GeometryBuilder vertex(float x, float y, float z, @Nullable Direction face, float nx, float ny, float nz,
                           TextureAtlasSprite sprite, float u, float v, int color);
}
