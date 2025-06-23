package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow public float yRot;
    @Shadow public float yRotO;
    
    @Unique
    private float automobility$lastYRot = 0f;

    @Inject(method = "setYRot", at = @At("HEAD"), cancellable = true)
    public void automobility$smoothYRotOnAutomobile(float yRot, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (self instanceof LocalPlayer player && player.isLocalPlayer() && 
            self.getVehicle() instanceof AutomobileEntity) {
            // 升平转插值系数，配平敏度于垂敏度
            // ↑↑↑ interpolation factor for horizontal rotation -> horizontal sensitivity ~ vertical sensitivity😀
            float smoothedYRot = Mth.rotLerp(1.0f, this.automobility$lastYRot, yRot);
            this.automobility$lastYRot = smoothedYRot;
            
            // 灭原调用，手动设平滑后角
            // ❌ original call and 🫳 set ∠ after smoothing
            ci.cancel();
            this.yRotO = this.yRot;
            this.yRot = smoothedYRot;
        } else {
            this.automobility$lastYRot = yRot;
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void automobility$clientFinalSyncBeforeDismountAutomobile(CallbackInfo ci) {
        var self = (Entity) (Object) this;
        if (!self.level().isClientSide()) {
            return;
        }

        if (self instanceof Player player && player.isLocalPlayer()) {
            var vehicle = player.getVehicle();

            if (vehicle instanceof AutomobileEntity auto && auto.isDriving(player)) {
                auto.clientOnAboutToDismount();
            }
        }
    }
}
