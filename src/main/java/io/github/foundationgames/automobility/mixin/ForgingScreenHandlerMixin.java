package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.screen.AutomobileScreenHandlerContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgingScreenHandler.class)
public abstract class ForgingScreenHandlerMixin {
    @Shadow @Final protected ScreenHandlerContext context;

    @Shadow protected abstract boolean canUse(BlockState state);

    @Inject(
            method = "canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void automobility$allowForgingScreenAutomobileSpoof(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (this.context instanceof AutomobileScreenHandlerContext ctx) {
            boolean isClose = ctx.get((world, pos) -> (player.getBlockPos().getSquaredDistance(pos) < 64), true);
            if (isClose && this.canUse(ctx.getAttachmentBlockState())) {
                cir.setReturnValue(true);
            }
        }
    }
}
