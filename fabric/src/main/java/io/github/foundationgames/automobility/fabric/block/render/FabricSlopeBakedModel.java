package io.github.foundationgames.automobility.fabric.block.render;

import io.github.foundationgames.automobility.block.model.SlopeBakedModel;
import io.github.foundationgames.automobility.block.model.SlopeUnbakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class FabricSlopeBakedModel extends SlopeBakedModel implements FabricBakedModel {
    public FabricSlopeBakedModel(TextureAtlasSprite frame, Map<BlockState, TextureAtlasSprite> frameTexOverrides, @Nullable TextureAtlasSprite plateInner,
                                 @Nullable TextureAtlasSprite plateOuter, ModelState settings, SlopeUnbakedModel.Type type) {
        super(frame, frameTexOverrides, plateInner, plateOuter, settings, type);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var geo = new FabricGeometryBuilder(context.getEmitter(), this.settings.getRotation().getMatrix());
        var frameSprite = this.getFrameSprite(level, pos);
        int frameColor = this.getFrameColor(level, pos);

        if (state.getBlock() instanceof HorizontalDirectionalBlock) {
            var dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            this.buildSlopeGeometry(frameSprite, geo, frameColor,
                    level.getBlockState(pos.relative(dir.getCounterClockWise(Direction.Axis.Y))) == state,
                    level.getBlockState(pos.relative(dir.getClockWise(Direction.Axis.Y))) == state);
        } else {
            this.buildSlopeGeometry(frameSprite, geo, frameColor, false, false);
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var geo = new FabricGeometryBuilder(context.getEmitter(), this.settings.getRotation().getMatrix());

        this.buildSlopeGeometry(this.getFrameSprite(null, null), geo,
                this.getFrameColor(null, null), false, false);
    }
}
