package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.automobile.attachment.rear.BaseChestRearAttachment;
import io.github.foundationgames.automobility.util.duck.EnderChestContainerDuck;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEnderChestContainer.class)
public class PlayerEnderChestContainerMixin implements EnderChestContainerDuck {
    private @Nullable BaseChestRearAttachment automobility$activeAttachment = null;

    @Override
    public void automobility$setActiveAttachment(BaseChestRearAttachment attachment) {
        this.automobility$activeAttachment = attachment;
    }

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    private void automobility$allowPlayerUseWithAttachment(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (this.automobility$activeAttachment != null) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "startOpen", at = @At("TAIL"))
    private void automobility$openActiveAttachment(Player player, CallbackInfo ci) {
        if (this.automobility$activeAttachment != null) {
            this.automobility$activeAttachment.open(player);
        }
    }

    @Inject(method = "stopOpen", at = @At("TAIL"))
    private void automobility$closeActiveAttachment(Player player, CallbackInfo ci) {
        if (this.automobility$activeAttachment != null) {
            this.automobility$activeAttachment.close(player);
        }
        this.automobility$activeAttachment = null;
    }
}
