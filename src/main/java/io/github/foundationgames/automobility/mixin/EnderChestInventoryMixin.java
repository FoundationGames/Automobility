package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.automobile.attachment.rear.BaseChestRearAttachment;
import io.github.foundationgames.automobility.util.duck.EnderChestInventoryDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderChestInventory.class)
public class EnderChestInventoryMixin implements EnderChestInventoryDuck {
    private @Nullable BaseChestRearAttachment automobility$activeAttachment = null;

    @Override
    public void automobility$setActiveAttachment(BaseChestRearAttachment attachment) {
        this.automobility$activeAttachment = attachment;
    }

    @Inject(method = "canPlayerUse", at = @At("HEAD"), cancellable = true)
    private void automobility$allowPlayerUseWithAttachment(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (this.automobility$activeAttachment != null) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onOpen", at = @At("TAIL"))
    private void automobility$openActiveAttachment(PlayerEntity player, CallbackInfo ci) {
        if (this.automobility$activeAttachment != null) {
            this.automobility$activeAttachment.open(player);
        }
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    private void automobility$closeActiveAttachment(PlayerEntity player, CallbackInfo ci) {
        if (this.automobility$activeAttachment != null) {
            this.automobility$activeAttachment.close(player);
        }
        this.automobility$activeAttachment = null;
    }
}
