package io.github.foundationgames.automobility.forge.block.render;

import io.github.foundationgames.automobility.block.model.SlopeBakedModel;
import io.github.foundationgames.automobility.block.model.SlopeUnbakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.ForgeConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ForgeSlopeBakedModel extends SlopeBakedModel {
    private static final ChunkRenderTypeSet RENDER_TYPES = ChunkRenderTypeSet.of(RenderType.translucent());
    private static final ModelProperty<TextureAtlasSprite> FRAME_SPRITE = new ModelProperty<>();
    private static final ModelProperty<Boolean> BORDERED_LEFT = new ModelProperty<>();
    private static final ModelProperty<Boolean> BORDERED_RIGHT = new ModelProperty<>();
    private static final ModelProperty<Integer> FRAME_COLOR = new ModelProperty<>();

    public ForgeSlopeBakedModel(TextureAtlasSprite frame, Map<BlockState, TextureAtlasSprite> frameTexOverrides, @Nullable TextureAtlasSprite plateInner,
                                @Nullable TextureAtlasSprite plateOuter, ModelState settings, SlopeUnbakedModel.Type type) {
        super(frame, frameTexOverrides, plateInner, plateOuter, settings, type);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return RENDER_TYPES;
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        var builder = modelData.derive().with(FRAME_SPRITE, this.getFrameSprite(level, pos))
                .with(FRAME_COLOR, this.getFrameColor(level, pos));

        if (state.getBlock() instanceof HorizontalDirectionalBlock) {
            var dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            builder.with(BORDERED_LEFT, level.getBlockState(pos.relative(dir.getCounterClockWise(Direction.Axis.Y))) == state)
                    .with(BORDERED_RIGHT, level.getBlockState(pos.relative(dir.getClockWise(Direction.Axis.Y))) == state);
        } else {
            builder.with(BORDERED_LEFT, false).with(BORDERED_RIGHT, false);
        }

        return builder.build();
    }

    private boolean unwrap(Boolean box) {
        if (box == null) {
            return false;
        }

        return box;
    }

    private int unwrap(Integer box) {
        if (box == null) {
            return 0xFFFFFFFF;
        }

        return box;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        if (renderType == null || this.getRenderTypes(state, rand, data).contains(renderType)) {
            var quads = new ArrayList<BakedQuad>();
            var geo = new ForgeGeometryBuilder(this.settings.getRotation().getMatrix(), side, quads);

            this.buildSlopeGeometry(data.get(FRAME_SPRITE), geo, unwrap(data.get(FRAME_COLOR)), unwrap(data.get(BORDERED_LEFT)), unwrap(data.get(BORDERED_RIGHT)));

            return quads;
        }

        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get();
    }
}
