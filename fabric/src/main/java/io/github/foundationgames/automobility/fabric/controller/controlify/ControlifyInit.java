package io.github.foundationgames.automobility.fabric.controller.controlify;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindingsApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.bindings.BindContext;
import dev.isxander.controlify.bindings.GamepadBinds;
import io.github.foundationgames.automobility.Automobility;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class ControlifyInit implements ControlifyEntrypoint {
    @Override
    public void onControlifyPreInit(ControlifyApi controlify) {
        BindContext drivingCtx = new BindContext(Automobility.rl("driving"), Set.of());
        Component category = Component.translatable("controlify.binding_category.driving");

        ControlifyCompat.accelerateBinding = ControlifyBindingsApi.get().registerBind(Automobility.rl("accelerate_automobile"), builder -> builder
                .defaultBind(GamepadBinds.A_BUTTON)
                .context(drivingCtx)
                .category(category));
        ControlifyCompat.brakeBinding = ControlifyBindingsApi.get().registerBind(Automobility.rl("brake_automobile"), builder -> builder
                .defaultBind(GamepadBinds.B_BUTTON)
                .context(drivingCtx)
                .category(category));
        ControlifyCompat.driftBinding = ControlifyBindingsApi.get().registerBind(Automobility.rl("drift_automobile"), builder -> builder
                .defaultBind(GamepadBinds.RIGHT_TRIGGER)
                .context(drivingCtx)
                .category(category));
    }

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {

    }
}
