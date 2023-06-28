package io.github.foundationgames.automobility.forge.mixin.jsonem;

import io.github.foundationgames.automobility.forge.vendored.jsonem.util.JsonEntityModelUtil;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin {
    @Shadow private Map<ModelLayerLocation, LayerDefinition> roots;

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void jsonem$loadJsonEntityModels(ResourceManager manager, CallbackInfo ci) {
        this.roots = new HashMap<>(this.roots);
        JsonEntityModelUtil.loadModels(manager, this.roots);
    }
}
