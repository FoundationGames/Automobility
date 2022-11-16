package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    private boolean automobility$cacheOnGround;

    @Shadow protected boolean onGround;

    @Inject(method = "collide", at = @At("HEAD"))
    private void automobility$spoofGroundStart(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        if (AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING) {
            this.automobility$cacheOnGround = this.onGround;
            this.onGround = true;
        }
    }

    @Inject(method = "collide", at = @At("TAIL"))
    private void automobility$spoofGroundEnd(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        if (AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING) {
            this.onGround = this.automobility$cacheOnGround;
            AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING = false;
        }
    }
}
