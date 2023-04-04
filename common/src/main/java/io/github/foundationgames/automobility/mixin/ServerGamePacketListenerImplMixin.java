package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V", shift = At.Shift.AFTER), cancellable = true)
    private void automobility$openAutomobileInventory(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
        var vehicle = this.player.getVehicle();
        if (packet.getAction() == ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY && vehicle instanceof AutomobileEntity automobile) {
            if (automobile.hasInventory()) {
                automobile.openInventory(this.player);
                ci.cancel();
            }
        }
    }
}
