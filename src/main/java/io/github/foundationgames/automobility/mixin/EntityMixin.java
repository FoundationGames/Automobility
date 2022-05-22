package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    private boolean automobility$cacheOnGround;

    @Shadow protected boolean onGround;

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
    private void automobility$spoofGroundStart(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING) {
            this.automobility$cacheOnGround = this.onGround;
            this.onGround = true;
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("TAIL"))
    private void automobility$spoofGroundEnd(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING) {
            this.onGround = this.automobility$cacheOnGround;
            AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING = false;
        }
    }
}
