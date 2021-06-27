package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.EmptyModel;
import io.github.foundationgames.automobility.automobile.render.wheel.ConvertibleWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.StandardWheelModel;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record AutomobileWheel(
        Identifier id,
        float size,
        WheelModel model,
        Ability ... abilities
) implements SimpleMapContentRegistry.Identifiable {

    public static final SimpleMapContentRegistry<AutomobileWheel> REGISTRY = new SimpleMapContentRegistry<>();

    private static final Identifier TEMP_ID = Automobility.id("temp");

    public static final AutomobileWheel STANDARD = REGISTRY.register(
            new AutomobileWheel(Automobility.id("standard"), 0.6f, new WheelModel(3, 3, Automobility.id("textures/entity/automobile/wheel/standard.png"), StandardWheelModel::new))
    );

    public static final AutomobileWheel OFF_ROAD = REGISTRY.register(
            new AutomobileWheel(Automobility.id("off_road"), 1.0f, new WheelModel(8, 5, TEMP_ID, EmptyModel::new))
    );

    public static final AutomobileWheel STEEL = REGISTRY.register(
            new AutomobileWheel(Automobility.id("steel"), 0.55f, new WheelModel(1.5f, 3, TEMP_ID, EmptyModel::new))
    );

    public static final AutomobileWheel INFLATABLE = REGISTRY.register(
            new AutomobileWheel(Automobility.id("inflatable"), 0.75f, new WheelModel(4, 4, TEMP_ID, EmptyModel::new), Ability.HYDROPLANE)
    );

    public static final AutomobileWheel CONVERTIBLE = REGISTRY.register(
            new AutomobileWheel(Automobility.id("convertible"), 0.75f, new WheelModel(5.2f, 4.1f, TEMP_ID, ConvertibleWheelModel::new))
    );

    @Override
    public Identifier getId() {
        return this.id;
    }

    public enum Ability {
        HYDROPLANE;
    }

    public static record WheelModel(
        float radius,
        float width,
        Identifier texture,
        Function<EntityRendererFactory.Context, Model> model
    ) {}
}
