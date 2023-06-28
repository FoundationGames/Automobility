package io.github.foundationgames.automobility.block.model;

import io.github.foundationgames.automobility.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SlopeBakedModel implements BakedModel {
    public static Factory impl = SlopeBakedModel::new;

    private final Map<BlockState, TextureAtlasSprite> frameTexOverrides;

    protected final @Nullable TextureAtlasSprite plateInner;
    protected final @Nullable TextureAtlasSprite plateOuter;
    protected final ModelState settings;
    protected final SlopeUnbakedModel.Type type;
    private final TextureAtlasSprite frame;

    public SlopeBakedModel(TextureAtlasSprite frame, Map<BlockState, TextureAtlasSprite> frameTexOverrides, @Nullable TextureAtlasSprite plateInner,
                           @Nullable TextureAtlasSprite plateOuter, ModelState settings, SlopeUnbakedModel.Type type) {
        this.frame = frame;
        this.frameTexOverrides = frameTexOverrides;
        this.plateInner = plateInner;
        this.plateOuter = plateOuter;
        this.settings = settings;
        this.type = type;
    }

    public TextureAtlasSprite getFrameSprite(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        if (level != null && pos != null) {
            var blockBelow = level.getBlockState(pos.below());

            if (frameTexOverrides.containsKey(blockBelow)) {
                return frameTexOverrides.get(blockBelow);
            }
            if (!blockBelow.isAir() && blockBelow.isCollisionShapeFullBlock(level, pos)) {
                return Minecraft.getInstance().getBlockRenderer().getBlockModel(blockBelow).getParticleIcon();
            }
        }

        return this.frame;
    }

    public int getFrameColor(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        if (level != null && pos != null) {
            var blockBelow = level.getBlockState(pos.below());
            var belowColor = Platform.get().blockColor(blockBelow);

            if (belowColor != null) {
                return belowColor.getColor(blockBelow, level, pos.below(), 0) | 0xFF000000;
            }
        }

        return 0xFFFFFFFF;
    }

    public void buildSlopeGeometry(TextureAtlasSprite sprite, GeometryBuilder geo, int frameColor, boolean borderedLeft, boolean borderedRight) {
        boolean steep = type == SlopeUnbakedModel.Type.STEEP;
        float height = steep ? 1 : 0.5f;
        float rise = steep ? 0 : (type == SlopeUnbakedModel.Type.TOP ? 0.5f : 0);
        boolean top = true;

        if (this.plateOuter != null && this.plateInner != null) {
            top = false;
            this.plate(height, rise, !borderedLeft, !borderedRight, this.plateInner, this.plateOuter, geo);
        }

        rightTriPrism(height, rise, frameColor, top, sprite, geo);

        float invRH = 1 - (rise + height);
        geo
                // South face
                .vertex(0, rise + height, 1, Direction.SOUTH, 0, 0, 1, sprite, 0, invRH, frameColor)
                .vertex(0, 0, 1, Direction.SOUTH, 0, 0, 1, sprite, 0, 1, frameColor)
                .vertex(1, 0, 1, Direction.SOUTH, 0, 0, 1, sprite, 1, 1, frameColor)
                .vertex(1, rise + height, 1, Direction.SOUTH, 0, 0, 1, sprite, 1, invRH, frameColor)

                // Bottom face
                .vertex(0, 0, 1, Direction.DOWN, 0, -1, 0, sprite, 0, 0, frameColor)
                .vertex(0, 0, 0, Direction.DOWN, 0, -1, 0, sprite, 0, 1, frameColor)
                .vertex(1, 0, 0, Direction.DOWN, 0, -1, 0, sprite, 1, 1, frameColor)
                .vertex(1, 0, 1, Direction.DOWN, 0, -1, 0, sprite, 1, 0, frameColor);

        if (rise > 0) {
            float invR = 1 - rise;
            geo
                    // West face
                    .vertex(0, rise, 0, Direction.WEST, -1, 0, 0, sprite, 0, invR, frameColor)
                    .vertex(0, 0, 0, Direction.WEST, -1, 0, 0, sprite, 0, 1, frameColor)
                    .vertex(0, 0, 1, Direction.WEST, -1, 0, 0, sprite, 1, 1, frameColor)
                    .vertex(0, rise, 1, Direction.WEST, -1, 0, 0, sprite, 1, invR, frameColor)

                    // East face
                    .vertex(1, rise, 1, Direction.EAST, 1, 0, 0, sprite, 1, invR, frameColor)
                    .vertex(1, 0, 1, Direction.EAST, 1, 0, 0, sprite, 1, 1, frameColor)
                    .vertex(1, 0, 0, Direction.EAST, 1, 0, 0, sprite, 0, 1, frameColor)
                    .vertex(1, rise, 0, Direction.EAST, 1, 0, 0, sprite, 0, invR, frameColor)

                    // North face
                    .vertex(1, rise, 0, Direction.NORTH, 0, 0, 1, sprite, 1, invR, frameColor)
                    .vertex(1, 0, 0, Direction.NORTH, 0, 0, 1, sprite, 1, 1, frameColor)
                    .vertex(0, 0, 0, Direction.NORTH, 0, 0, 1, sprite, 0, 1, frameColor)
                    .vertex(0, rise, 0, Direction.NORTH, 0, 0, 1, sprite, 0, invR, frameColor);
        }
    }

    private void rightTriPrism(float height, float rise, int color, boolean top, TextureAtlasSprite sprite, GeometryBuilder geo) {
        float invR = 1 - rise;
        float invRH = 1 - (rise + height);
        var topNormal = new Vector3f(0, 1, -height); // This actually makes sense math wise don't worry about it
        topNormal.normalize();

        geo
                // West triangle
                .vertex(0, rise + height, 1, Direction.WEST, -1, 0, 0, sprite, 1, invRH, color)
                .vertex(0, rise, 0, Direction.WEST, -1, 0, 0, sprite, 0, invR, color)
                .vertex(0, rise, 1, Direction.WEST, -1, 0, 0, sprite, 1, invR, color)
                .vertex(0, rise + height, 1, Direction.WEST, -1, 0, 0, sprite, 1, invRH, color)

                // East triangle
                .vertex(1, rise + height, 1, Direction.EAST, 1, 0, 0, sprite, 0, invRH, color)
                .vertex(1, rise + height, 1, Direction.EAST, 1, 0, 0, sprite, 0, invRH, color)
                .vertex(1, rise, 1, Direction.EAST, 1, 0, 0, sprite, 0, invR, color)
                .vertex(1, rise, 0, Direction.EAST, 1, 0, 0, sprite, 1, invR, color);

        if (top) {
            geo     // Top face
                    .vertex(0, rise, 0, null, topNormal.x(), topNormal.y(), topNormal.z(), sprite, 1, 1, color)
                    .vertex(0, rise + height, 1, null, topNormal.x(), topNormal.y(), topNormal.z(), sprite, 1, 0, color)
                    .vertex(1, rise + height, 1, null, topNormal.x(), topNormal.y(), topNormal.z(), sprite, 0, 0, color)
                    .vertex(1, rise, 0, null, topNormal.x(), topNormal.y(), topNormal.z(), sprite, 0, 1, color);
        }
    }

    private void plate(float height, float rise, boolean left, boolean right, TextureAtlasSprite plateInner, TextureAtlasSprite plateOuter, GeometryBuilder geo) {
        var topNormal = new Vector3f(0, 1, -height);
        topNormal.normalize();
        var northNormal = new Vector3f(0, -height, 1);
        northNormal.normalize();
        var southNormal = new Vector3f(0, height, 1);
        southNormal.normalize();

        var topFaceOffset = new Vector3f(topNormal); // Translate from the surface of the slope to the surface of the plate
        topFaceOffset.mul(0.0625f);
        var onePxUp = new Vector3f(southNormal); // Translate one pixel up the slope
        onePxUp.normalize();
        onePxUp.mul(0.0625f);

        geo
                // Top face inner
                .vertex(right ? 0.9375f : 1, 0.001f + rise + topFaceOffset.y() + onePxUp.y(), topFaceOffset.z() + onePxUp.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateInner, right ? 0.0625f : 0, 0.9375f)
                .vertex(left ? 0.0625f : 0, 0.001f + rise + topFaceOffset.y() + onePxUp.y(), topFaceOffset.z() + onePxUp.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateInner, left ? 0.9375f : 1, 0.9375f)
                .vertex(left ? 0.0625f : 0, (0.001f + rise + height + topFaceOffset.y()) - onePxUp.y(), (1 + topFaceOffset.z()) - onePxUp.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateInner, left ? 0.9375f : 1, 0.0625f)
                .vertex(right ? 0.9375f : 1, (0.001f + rise + height + topFaceOffset.y()) - onePxUp.y(), (1 + topFaceOffset.z()) - onePxUp.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateInner, right ? 0.0625f : 0, 0.0625f)

                // Top face outer
                .vertex(1, rise + topFaceOffset.y(), topFaceOffset.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateOuter, 0, 1)
                .vertex(0, rise + topFaceOffset.y(), topFaceOffset.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateOuter, 1, 1)
                .vertex(0, rise + height + topFaceOffset.y(), 1 + topFaceOffset.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateOuter, 1, 0)
                .vertex(1, rise + height + topFaceOffset.y(), 1 + topFaceOffset.z(), null, topNormal.x(), topNormal.y(), topNormal.z(), plateOuter, 0, 0)

                // North face
                .vertex(1, rise, 0, null, northNormal.x(), northNormal.y(), northNormal.z(), plateOuter, 0, 1)
                .vertex(0, rise, 0, null, northNormal.x(), northNormal.y(), northNormal.z(), plateOuter, 1, 1)
                .vertex(0, rise + topFaceOffset.y(), topFaceOffset.z(), null, northNormal.x(), northNormal.y(), northNormal.z(), plateOuter, 1, 0.9375f)
                .vertex(1, rise + topFaceOffset.y(), topFaceOffset.z(), null, northNormal.x(), northNormal.y(), northNormal.z(), plateOuter, 0, 0.9375f)

                // South face
                .vertex(1, rise + height + topFaceOffset.y(), 1 + topFaceOffset.z(), null, southNormal.x(), southNormal.y(), southNormal.z(), plateOuter, 0, 0)
                .vertex(0, rise + height + topFaceOffset.y(), 1 + topFaceOffset.z(), null, southNormal.x(), southNormal.y(), southNormal.z(), plateOuter, 1, 0)
                .vertex(0, rise + height, 1, null, southNormal.x(), southNormal.y(), southNormal.z(), plateOuter, 1, 0.0625f)
                .vertex(1, rise + height, 1, null, southNormal.x(), southNormal.y(), southNormal.z(), plateOuter, 0, 0.0625f)

                // East face
                .vertex(1, rise + height, 1, null, 1, 0, 0, plateOuter, 0, 0)
                .vertex(1, rise, 0, null, 1, 0, 0, plateOuter, 0, 1)
                .vertex(1, rise + topFaceOffset.y(), topFaceOffset.z(), null, 1, 0, 0, plateOuter, 0.0625f, 1)
                .vertex(1, rise + height + topFaceOffset.y(), 1 + topFaceOffset.z(), null, 1, 0, 0, plateOuter, 0.0625f, 0)

                // West face
                .vertex(0, rise + height + topFaceOffset.y(), 1 + topFaceOffset.z(), null, -1, 0, 0, plateOuter, 1, 0)
                .vertex(0, rise + topFaceOffset.y(), topFaceOffset.z(), null, -1, 0, 0, plateOuter, 1, 1)
                .vertex(0, rise, 0, null, -1, 0, 0, plateOuter, 0.9375f, 1)
                .vertex(0, rise + height, 1, null, -1, 0, 0, plateOuter, 0.9375f, 0)
        ;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.getFrameSprite(null, null);
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    public interface Factory {
        SlopeBakedModel create(TextureAtlasSprite frame, Map<BlockState, TextureAtlasSprite> frameTexOverrides, @Nullable TextureAtlasSprite plateInner,
                               @Nullable TextureAtlasSprite plateOuter, ModelState settings, SlopeUnbakedModel.Type type);
    }
}
