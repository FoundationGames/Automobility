package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.engine.IronEngineModel;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record AutomobileEngine(
        Identifier id,
        float torque,
        EngineModel model
) implements SimpleMapContentRegistry.Identifiable {

    public static final SimpleMapContentRegistry<AutomobileEngine> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileEngine IRON = REGISTRY.register(
            new AutomobileEngine(Automobility.id("iron"), 0.5f,
                    new EngineModel(Automobility.id("textures/entity/automobile/engine/iron.png"), IronEngineModel::new)
            )
    );

    @Override
    public Identifier getId() {
        return this.id;
    }

    public static record EngineModel(
            Identifier texture,
            Function<EntityRendererFactory.Context, Model> model,
            ExhaustPos ... exhausts
    ) {}

    public static record ExhaustPos(
            float x, float y, float z,
            float pitch, float yaw
    ) {}
}
