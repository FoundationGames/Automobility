package io.github.foundationgames.automobility.mixin.myron;

import dev.monarkhes.myron.impl.client.obj.AbstractObjLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = AbstractObjLoader.class, remap = false)
public class AbstractObjLoaderMixin {
    // TODO: REMOVE THIS when an actual fix to myron is made available
    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void automobility$TEMPORARY_FIX_FOR_CREATE_REMOVE_LATER(
            ResourceManager resourceManager, Identifier identifier, ModelTransformation transformation,
            boolean isSideLit, CallbackInfoReturnable<@Nullable UnbakedModel> cir) {
        if ("create".equals(identifier.getNamespace())) {
            cir.setReturnValue(null);
        }
    }
}
