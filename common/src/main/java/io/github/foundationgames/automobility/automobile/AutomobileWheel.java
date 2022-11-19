package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public record AutomobileWheel(
        ResourceLocation id,
        float size,
        float grip,
        WheelModel model,
        Ability ... abilities
) implements AutomobileComponent<AutomobileWheel> {
    public static final ResourceLocation ID = Automobility.rl("wheel");
    public static final SimpleMapContentRegistry<AutomobileWheel> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileWheel EMPTY = REGISTRY.register(
            new AutomobileWheel(Automobility.rl("empty"), 0.01f, 0.01f, new WheelModel(1, 1, new ResourceLocation("empty"), Automobility.rl("empty")))
    );

    public static final AutomobileWheel STANDARD = REGISTRY.register(
            new AutomobileWheel(Automobility.rl("standard"), 0.6f, 0.5f, new WheelModel(3, 3, Automobility.rl("textures/entity/automobile/wheel/standard.png"), Automobility.rl("wheel_standard")))
    );

    public static final AutomobileWheel OFF_ROAD = REGISTRY.register(
            new AutomobileWheel(Automobility.rl("off_road"), 1.1f, 0.8f, new WheelModel(8.4f, 5, Automobility.rl("textures/entity/automobile/wheel/off_road.png"), Automobility.rl("wheel_off_road")))
    );

    public static final AutomobileWheel STEEL = REGISTRY.register(
            new AutomobileWheel(Automobility.rl("steel"), 0.69f, 0.4f, new WheelModel(3.625f, 3, Automobility.rl("textures/entity/automobile/wheel/steel.png"), Automobility.rl("wheel_steel")))
    );

    public static final AutomobileWheel TRACTOR = REGISTRY.register(
            new AutomobileWheel(Automobility.rl("tractor"), 1.05f, 0.69f, new WheelModel(3.625f, 3, Automobility.rl("textures/entity/automobile/wheel/tractor.png"), Automobility.rl("wheel_tractor")))
    );

    public static final AutomobileWheel CARRIAGE = REGISTRY.register(carriage("carriage", 0.2f));
    public static final AutomobileWheel PLATED = REGISTRY.register(carriage("plated", 0.33f));
    public static final AutomobileWheel STREET = REGISTRY.register(carriage("street", 0.5f));
    public static final AutomobileWheel GILDED = REGISTRY.register(carriage("gilded", 0.45f));
    public static final AutomobileWheel BEJEWELED = REGISTRY.register(carriage("bejeweled", 0.475f));

    public static final AutomobileWheel CONVERTIBLE = REGISTRY.register(
            new AutomobileWheel(Automobility.rl("convertible"), 0.75f, 0.45f, new WheelModel(5.2f, 4.1f, Automobility.rl("textures/entity/automobile/frame/c_arr.png"), Automobility.rl("wheel_convertible")))
    );

    private static AutomobileWheel carriage(String name, float grip) {
        return new AutomobileWheel(Automobility.rl(name), 1.05f, grip, new WheelModel(5, 2, Automobility.rl("textures/entity/automobile/wheel/"+name+".png"), Automobility.rl("wheel_carriage")));
    }

    public static final DisplayStat<AutomobileWheel> STAT_SIZE = new DisplayStat<>("size", AutomobileWheel::size);
    public static final DisplayStat<AutomobileWheel> STAT_GRIP = new DisplayStat<>("grip", AutomobileWheel::grip);

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public ResourceLocation containerId() {
        return ID;
    }

    @Override
    public void forEachStat(Consumer<DisplayStat<AutomobileWheel>> action) {
        action.accept(STAT_SIZE);
        action.accept(STAT_GRIP);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return "wheel."+id.getNamespace()+"."+id.getPath();
    }

    public enum Ability {
    }

    public static record WheelModel(
        float radius,
        float width,
        ResourceLocation texture,
        ResourceLocation modelId
    ) {}
}
