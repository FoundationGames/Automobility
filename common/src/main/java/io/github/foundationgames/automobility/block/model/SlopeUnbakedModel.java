package io.github.foundationgames.automobility.block.model;

import com.google.common.collect.ImmutableMap;
import io.github.foundationgames.automobility.Automobility;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class SlopeUnbakedModel implements UnbakedModel {
    public static final ResourceLocation MODEL_SLOPE_BOTTOM = Automobility.rl("block/slope_bottom");
    public static final ResourceLocation MODEL_SLOPE_TOP = Automobility.rl("block/slope_top");
    public static final ResourceLocation MODEL_STEEP_SLOPE = Automobility.rl("block/steep_slope");
    public static final ResourceLocation MODEL_SLOPE_BOTTOM_DASH_PANEL = Automobility.rl("block/slope_bottom_dash_panel");
    public static final ResourceLocation MODEL_SLOPE_TOP_DASH_PANEL = Automobility.rl("block/slope_top_dash_panel");
    public static final ResourceLocation MODEL_STEEP_SLOPE_DASH_PANEL = Automobility.rl("block/steep_slope_dash_panel");
    public static final ResourceLocation MODEL_SLOPE_BOTTOM_DASH_PANEL_OFF = Automobility.rl("block/slope_bottom_dash_panel_off");
    public static final ResourceLocation MODEL_SLOPE_TOP_DASH_PANEL_OFF = Automobility.rl("block/slope_top_dash_panel_off");
    public static final ResourceLocation MODEL_STEEP_SLOPE_DASH_PANEL_OFF = Automobility.rl("block/steep_slope_dash_panel_off");

    public static final ResourceLocation TEX_FRAME = Automobility.rl("block/slope_frame");
    public static final ResourceLocation TEX_DASH_PANEL = Automobility.rl("block/dash_panel");
    public static final ResourceLocation TEX_DASH_PANEL_OFF = Automobility.rl("block/dash_panel_off");
    public static final ResourceLocation TEX_DASH_PANEL_FRAME = Automobility.rl("block/dash_panel_frame");

    public static final Map<ResourceLocation, Supplier<SlopeUnbakedModel>> DEFAULT_MODELS = ImmutableMap.of(
            MODEL_SLOPE_TOP, () -> new SlopeUnbakedModel(Type.TOP, TEX_FRAME, null, null),
            MODEL_SLOPE_BOTTOM, () -> new SlopeUnbakedModel(Type.BOTTOM, TEX_FRAME, null, null),
            MODEL_STEEP_SLOPE, () -> new SlopeUnbakedModel(Type.STEEP, TEX_FRAME, null, null),
            MODEL_SLOPE_TOP_DASH_PANEL, () -> new SlopeUnbakedModel(Type.TOP, TEX_FRAME, TEX_DASH_PANEL, TEX_DASH_PANEL_FRAME),
            MODEL_SLOPE_BOTTOM_DASH_PANEL, () -> new SlopeUnbakedModel(Type.BOTTOM, TEX_FRAME, TEX_DASH_PANEL, TEX_DASH_PANEL_FRAME),
            MODEL_STEEP_SLOPE_DASH_PANEL, () -> new SlopeUnbakedModel(Type.STEEP, TEX_FRAME, TEX_DASH_PANEL, TEX_DASH_PANEL_FRAME),
            MODEL_SLOPE_TOP_DASH_PANEL_OFF, () -> new SlopeUnbakedModel(Type.TOP, TEX_FRAME, TEX_DASH_PANEL_OFF, TEX_DASH_PANEL_FRAME),
            MODEL_SLOPE_BOTTOM_DASH_PANEL_OFF, () -> new SlopeUnbakedModel(Type.BOTTOM, TEX_FRAME, TEX_DASH_PANEL_OFF, TEX_DASH_PANEL_FRAME),
            MODEL_STEEP_SLOPE_DASH_PANEL_OFF, () -> new SlopeUnbakedModel(Type.STEEP, TEX_FRAME, TEX_DASH_PANEL_OFF, TEX_DASH_PANEL_FRAME)
    );

    private static final ResourceLocation PARENT = new ResourceLocation("block/block");

    private final Type type;
    private final Material frameTex;
    private final @Nullable Material plateInnerTex;
    private final @Nullable Material plateOuterTex;

    public SlopeUnbakedModel(Type type, ResourceLocation frameTex, @Nullable ResourceLocation plateInnerTex,
                             @Nullable ResourceLocation plateOuterTex) {
        this.type = type;
        this.frameTex = new Material(TextureAtlas.LOCATION_BLOCKS, frameTex);
        this.plateInnerTex = plateInnerTex != null ? new Material(TextureAtlas.LOCATION_BLOCKS, plateInnerTex) : null;
        this.plateOuterTex = plateOuterTex != null ? new Material(TextureAtlas.LOCATION_BLOCKS, plateOuterTex) : null;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of(PARENT);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
    }

    // TODO: Something better than this that supports other mods and resource packs
    private static Map<BlockState, TextureAtlasSprite> createFrameTexOverrides(Function<Material, TextureAtlasSprite> spriteGet) {
        return ImmutableMap.of(
                Blocks.GRASS_BLOCK.defaultBlockState(), vanillaSprite(spriteGet, "block/grass_block_top"),
                Blocks.PODZOL.defaultBlockState(), vanillaSprite(spriteGet, "block/podzol_top"),
                Blocks.MYCELIUM.defaultBlockState(), vanillaSprite(spriteGet, "block/mycelium_top"),
                Blocks.CRIMSON_NYLIUM.defaultBlockState(), vanillaSprite(spriteGet, "block/crimson_nylium"),
                Blocks.WARPED_NYLIUM.defaultBlockState(), vanillaSprite(spriteGet, "block/warped_nylium")
        );
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation) {
        return SlopeBakedModel.impl.create(function.apply(frameTex), createFrameTexOverrides(function),
                plateInnerTex != null ? function.apply(plateInnerTex) : null,
                plateOuterTex != null ? function.apply(plateOuterTex) : null, modelState, type);
    }

    private static TextureAtlasSprite vanillaSprite(Function<Material, TextureAtlasSprite> spriteGet, String name) {
        return spriteGet.apply(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("minecraft", name)));
    }

    public enum Type {
        BOTTOM, TOP, STEEP
    }
}
