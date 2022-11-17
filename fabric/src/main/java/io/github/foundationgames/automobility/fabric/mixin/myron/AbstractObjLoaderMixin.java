package io.github.foundationgames.automobility.fabric.mixin.myron;

//@Pseudo
//@Mixin(value = AbstractObjLoader.class, remap = false)
public class AbstractObjLoaderMixin {
    /*
    // TODO: REMOVE THIS when an actual fix to myron is made available
    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void automobility$TEMPORARY_FIX_FOR_CREATE_REMOVE_LATER(
            ResourceManager resourceManager, Identifier identifier, ModelTransformation transformation,
            boolean isSideLit, CallbackInfoReturnable<@Nullable UnbakedModel> cir) {
        if ("create".equals(identifier.getNamespace())) {
            cir.setReturnValue(null);
        }
    }
     */
}
