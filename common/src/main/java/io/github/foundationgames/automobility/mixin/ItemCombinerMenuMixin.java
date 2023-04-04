package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.screen.AutomobileContainerLevelAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMenuMixin {
    @Shadow @Final protected ContainerLevelAccess access;

    @Shadow protected abstract boolean isValidBlock(BlockState state);

    @Inject(
            method = "stillValid",
            at = @At("HEAD"), cancellable = true)
    private void automobility$allowSmithingMenuAutomobileSpoof(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (this.access instanceof AutomobileContainerLevelAccess access) {
            boolean isClose = access.evaluate((world, pos) -> (player.blockPosition().distSqr(pos) < 64), true);
            if (isClose && this.isValidBlock(access.getAttachmentBlockState())) {
                cir.setReturnValue(true);
            }
        }
    }
}
