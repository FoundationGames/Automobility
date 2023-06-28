package io.github.foundationgames.automobility.forge.block.render;

import io.github.foundationgames.automobility.block.model.GeometryBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.common.ForgeConfig;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class ForgeGeometryBuilder implements GeometryBuilder {
    private final Matrix4f transform;
    private final @Nullable Direction filter;
    private final QuadBakingVertexConsumer quads;

    public ForgeGeometryBuilder(Matrix4f transform, @Nullable Direction filter, List<BakedQuad> quadPool) {
        this.transform = transform;
        this.filter = filter;

        this.quads = new QuadBakingVertexConsumer(quadPool::add);
        this.quads.setShade(true);
        this.quads.setHasAmbientOcclusion(ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get());
    }

    @Override
    public GeometryBuilder vertex(float x, float y, float z, @Nullable Direction face, float nx, float ny, float nz, TextureAtlasSprite sprite, float u, float v) {
        return this.vertex(x, y, z, face, nx, ny, nz, sprite, u, v, 0xFFFFFFFF);
    }

    @Override
    public GeometryBuilder vertex(float x, float y, float z, @Nullable Direction face, float nx, float ny, float nz, TextureAtlasSprite sprite, float u, float v, int color) {
        if (face != null) {
            face = Direction.rotate(this.transform, face);
        }
        if (face != this.filter) {
            return this;
        }

        var pos = new Vector4f(x - 0.5f, y, z - 0.5f, 1);
        var tNormal = new Vector4f(nx, ny, nz, 1);
        pos.mul(this.transform);
        tNormal.mul(this.transform); // This is under the assumption that transform will always be a rotation

        var normal = new Vector3f(tNormal.x(), tNormal.y(), tNormal.z());
        normal.normalize();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        quads.setDirection(face != null ? face : Direction.UP);
        quads.setSprite(sprite);
        quads.vertex(pos.x() + 0.5f, pos.y(), pos.z() + 0.5f).color(color).uv(u0 + ((u1 - u0) * u), v0 + ((v1 - v0) * v)).normal(normal.x(), normal.y(), normal.z()).endVertex();

        return this;
    }
}
