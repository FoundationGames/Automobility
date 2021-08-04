package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
            new AutomobileWheel(Automobility.id("standard"), 0.6f, 0.5f, new WheelModel(3, 3, Automobility.id("textures/entity/automobile/wheel/standard.png"), Automobility.id("wheel_standard")))
    );

    public static final AutomobileWheel OFF_ROAD = REGISTRY.register(
            new AutomobileWheel(Automobility.id("off_road"), 1.1f, 0.8f, new WheelModel(8.4f, 5, Automobility.id("textures/entity/automobile/wheel/off_road.png"), Automobility.id("wheel_off_road")))
    );

    public static final AutomobileWheel STEEL = REGISTRY.register(
            new AutomobileWheel(Automobility.id("steel"), 0.69f, 0.4f, new WheelModel(3.625f, 3, Automobility.id("textures/entity/automobile/wheel/steel.png"), Automobility.id("wheel_steel")))
    );

    public static final AutomobileWheel TRACTOR = REGISTRY.register(
            new AutomobileWheel(Automobility.id("tractor"), 1.05f, 0.69f, new WheelModel(3.625f, 3, Automobility.id("textures/entity/automobile/wheel/tractor.png"), Automobility.id("wheel_tractor")))
    );

    public static final AutomobileWheel BUGGY = REGISTRY.register(
            new AutomobileWheel(Automobility.id("buggy"), 1.17f, 0.75f, new WheelModel(7.92f, 5.36f, Automobility.id("textures/entity/automobile/wheel/buggy.png"), Automobility.id("wheel_buggy")))
    );

    public static final AutomobileWheel CONVERTIBLE = REGISTRY.register(
            new AutomobileWheel(Automobility.id("convertible"), 0.75f, 0.45f, new WheelModel(5.2f, 4.1f, Automobility.id("textures/entity/automobile/frame/dababy.png"), Automobility.id("wheel_convertible")))
    );

    @Override
    public Identifier getId() {
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
        Identifier texture,
        Identifier modelId
    ) {
        @Environment(EnvType.CLIENT)
        public Function<EntityRendererFactory.Context, Model> model() {
            return AutomobilityModels.MODELS.get(modelId);
        }
    }
}
