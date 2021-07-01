package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.wheel.ConvertibleWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.OffRoadWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.StandardWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.SteelWheelModel;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record AutomobileWheel(
        Identifier id,
        float size,
        float grip,
        WheelModel model,
        Ability ... abilities
) implements SimpleMapContentRegistry.Identifiable {

    public static final SimpleMapContentRegistry<AutomobileWheel> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileWheel STANDARD = REGISTRY.register(
            new AutomobileWheel(Automobility.id("standard"), 0.6f, 0.5f, new WheelModel(3, 3, Automobility.id("textures/entity/automobile/wheel/standard.png"), StandardWheelModel::new))
    );

    public static final AutomobileWheel OFF_ROAD = REGISTRY.register(
            new AutomobileWheel(Automobility.id("off_road"), 1.0f, 0.8f, new WheelModel(8.4f, 5, Automobility.id("textures/entity/automobile/wheel/off_road.png"), OffRoadWheelModel::new))
    );

    public static final AutomobileWheel STEEL = REGISTRY.register(
            new AutomobileWheel(Automobility.id("steel"), 0.69f, 0.4f, new WheelModel(3.625f, 3, Automobility.id("textures/entity/automobile/wheel/steel.png"), SteelWheelModel::new))
    );

    // public static final AutomobileWheel INFLATABLE = REGISTRY.register(
    //         new AutomobileWheel(Automobility.id("inflatable"), 0.75f, new WheelModel(4, 4, TEMP_ID, EmptyModel::new), Ability.HYDROPLANE)
    // );

    public static final AutomobileWheel CONVERTIBLE = REGISTRY.register(
            new AutomobileWheel(Automobility.id("convertible"), 0.75f, 0.45f, new WheelModel(5.2f, 4.1f, Automobility.id("textures/entity/automobile/frame/dababy.png"), ConvertibleWheelModel::new))
    );

    @Override
    public Identifier getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return "wheel."+id.getNamespace()+"."+id.getPath();
    }

    public enum Ability {
        // HYDROPLANE;
    }

    public static record WheelModel(
        float radius,
        float width,
        Identifier texture,
        Function<EntityRendererFactory.Context, Model> model
    ) {}
}
