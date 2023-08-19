package io.github.foundationgames.automobility.fabric.controller.controlify;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.BindingSupplier;
import dev.isxander.controlify.api.bind.ControlifyBindingsApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.event.ControlifyEvents;
import dev.isxander.controlify.api.guide.ActionPriority;
import dev.isxander.controlify.api.ingameguide.ActionLocation;
import dev.isxander.controlify.bindings.BindContext;
import dev.isxander.controlify.bindings.GamepadBinds;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ControlifyCompat implements ControlifyEntrypoint {
    public static final Set<BindingSupplier> AUTOMOBILITY_BINDINGS = new HashSet<>();

    @Override
    public void onControlifyPreInit(ControlifyApi controlify) {
        BindContext drivingCtx = new BindContext(Automobility.rl("driving"), Set.of());
        Component category = Component.translatable("controlify.binding_category.driving");

        ControlifyController.accelerateBinding = ControlifyBindingsApi.get().registerBind(Automobility.rl("accelerate_automobile"), builder -> builder
                .defaultBind(GamepadBinds.A_BUTTON)
                .context(drivingCtx)
                .category(category));
        ControlifyController.brakeBinding = ControlifyBindingsApi.get().registerBind(Automobility.rl("brake_automobile"), builder -> builder
                .defaultBind(GamepadBinds.B_BUTTON)
                .context(drivingCtx)
                .category(category));
        ControlifyController.driftBinding = ControlifyBindingsApi.get().registerBind(Automobility.rl("drift_automobile"), builder -> builder
                .defaultBind(GamepadBinds.RIGHT_TRIGGER)
                .context(drivingCtx)
                .category(category));

        AUTOMOBILITY_BINDINGS.clear();
        AUTOMOBILITY_BINDINGS.add(ControlifyController.accelerateBinding);
        AUTOMOBILITY_BINDINGS.add(ControlifyController.brakeBinding);
        AUTOMOBILITY_BINDINGS.add(ControlifyController.driftBinding);

        ControlifyEvents.INGAME_GUIDE_REGISTRY.register((bindings, registry) -> {
            var accelerate = bindings.get(Automobility.rl("accelerate_automobile"));
            var brake = bindings.get(Automobility.rl("brake_automobile"));
            var drift = bindings.get(Automobility.rl("drift_automobile"));

            registry.registerGuideAction(accelerate, ActionLocation.LEFT, ActionPriority.LOW, ctx -> {
                if (ctx.player().getVehicle() instanceof AutomobileEntity)
                    return Optional.of(Component.translatable("controlify.binding.automobility.accelerate_automobile"));
                return Optional.empty();
            });
            registry.registerGuideAction(brake, ActionLocation.LEFT, ActionPriority.LOW, ctx -> {
                if (ctx.player().getVehicle() instanceof AutomobileEntity)
                    return Optional.of(Component.translatable("controlify.binding.automobility.brake_automobile"));
                return Optional.empty();
            });
            registry.registerGuideAction(drift, ActionLocation.LEFT, ActionPriority.LOW, ctx -> {
                if (ctx.player().getVehicle() instanceof AutomobileEntity)
                    return Optional.of(Component.translatable("controlify.binding.automobility.drift_automobile"));
                return Optional.empty();
            });
        });
    }

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {

    }
}
