package io.github.foundationgames.automobility.fabric;

import io.github.foundationgames.automobility.AutomobilityClient;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.fabric.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.screen.AutomobileHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;

public class AutomobilityClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricIntermediary.init();
        AutomobilityClient.init();

        AutomobilityFabric.register(Registry.PARTICLE_TYPE);

        AutomobilityAssets.setup();

        HudRenderCallback.EVENT.register((pose, tickDelta) -> {
            var player = Minecraft.getInstance().player;
            if (player.getVehicle() instanceof AutomobileEntity auto) {
                AutomobileHud.render(pose, player, auto, tickDelta);
            }
        });
    }
}
