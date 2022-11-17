package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.screen.AutomobileContainerLevelAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(
            method = "stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void automobility$spoofAutomobileAttachmentScreens(ContainerLevelAccess context, Player player, Block block, CallbackInfoReturnable<Boolean> cir) {
        if (context instanceof AutomobileContainerLevelAccess ctx) {
            boolean isClose = ctx.evaluate((world, pos) -> (player.blockPosition().distSqr(pos) < 64), true);
            if (isClose && ctx.getAttachmentBlockState().is(block)) {
                cir.setReturnValue(true);
            }
        }
    }
}
