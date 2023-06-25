package io.github.foundationgames.automobility.fabric.block.render;

import io.github.foundationgames.automobility.block.model.GeometryBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FabricGeometryBuilder implements GeometryBuilder {
    private final QuadEmitter quads;
    private final Matrix4f transform;

    private int index = 0;

    public FabricGeometryBuilder(QuadEmitter quads, Matrix4f transform) {
        this.quads = quads;
        this.transform = transform;
    }

    @Override
    public GeometryBuilder vertex(float x, float y, float z, @Nullable Direction face, float nx, float ny, float nz, TextureAtlasSprite sprite, float u, float v) {
        return this.vertex(x, y, z, face, nx, ny, nz, sprite, u, v, 0xFFFFFFFF);
    }

    @Override
    public GeometryBuilder vertex(float x, float y, float z, @Nullable Direction face, float nx, float ny, float nz, TextureAtlasSprite sprite, float u, float v, int color) {
        var pos = new Vector4f(x - 0.5f, y, z - 0.5f, 1);
        var tNormal = new Vector4f(nx, ny, nz, 1);
        pos.mul(this.transform);
        tNormal.mul(this.transform); // This is under the assumption that transform will always be a rotation

        var normal = new Vector3f(tNormal.x(), tNormal.y(), tNormal.z());
        normal.normalize();

        quads.pos(index, pos.x() + 0.5f, pos.y(), pos.z() + 0.5f);
        if (face != null) {
            face = Direction.rotate(this.transform, face);
            quads.cullFace(face);
        }
        quads.normal(index, normal.x(), normal.y(), normal.z());

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        quads.spriteColor(index, 0, color);
        quads.sprite(index, 0, u0 + ((u1 - u0) * u), v0 + ((v1 - v0) * v));

        if (++index >= 4) {
            quads.emit();
            index = 0;
        }

        return this;
    }
}
