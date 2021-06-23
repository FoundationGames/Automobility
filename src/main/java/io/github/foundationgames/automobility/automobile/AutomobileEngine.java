package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

// TODO
public record AutomobileEngine(
        Identifier id,
        float torque,
        EngineModel model
) implements SimpleMapContentRegistry.Identifiable {

    public static final SimpleMapContentRegistry<AutomobileEngine> REGISTRY = new SimpleMapContentRegistry<>();

    @Override
    public Identifier getId() {
        return this.id;
    }

    public static record EngineModel(
            Identifier texture,
            Function<EntityRendererFactory.Context, Model> model,
            Vec3d ... exhausts
    ) {}
}
