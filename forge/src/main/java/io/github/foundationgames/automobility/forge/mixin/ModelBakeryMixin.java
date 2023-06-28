package io.github.foundationgames.automobility.forge.mixin;

import io.github.foundationgames.automobility.block.model.SlopeUnbakedModel;
import io.github.foundationgames.automobility.util.InitlessConstants;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void automobility$addUnbakedSlopeModels(ResourceLocation location, CallbackInfo ci) {
        // Only run this handler if the mod is actually loaded
        if (ModList.get().isLoaded(InitlessConstants.AUTOMOBILITY)) {
            if (SlopeUnbakedModel.DEFAULT_MODELS.containsKey(location)) {
                var model = SlopeUnbakedModel.DEFAULT_MODELS.get(location).get();
                this.unbakedCache.put(location, model);
                this.topLevelModels.put(location, model);

                ci.cancel();
            }
        }
    }
}
