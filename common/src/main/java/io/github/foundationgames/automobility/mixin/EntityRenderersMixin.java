package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderers.class)
public class EntityRenderersMixin {
    @Inject(method = "createEntityRenderers", at = @At("HEAD"))
    private static void automobility$reloadContextListeners(EntityRendererProvider.Context ctx, CallbackInfoReturnable<Map<EntityType<?>, EntityRenderer<?>>> cir) {
        EntityRenderHelper.reload(ctx);
    }
}
