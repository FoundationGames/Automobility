package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.EmptyModel;
import io.github.foundationgames.automobility.automobile.render.frame.StandardFrameModel;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record AutomobileFrame(
        Identifier id,
        float weight,
        FrameModel model
) implements SimpleMapContentRegistry.Identifiable {

    public static final SimpleMapContentRegistry<AutomobileFrame> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileFrame STANDARD_BLUE = REGISTRY.register(
            new AutomobileFrame(
                    Automobility.id("standard_blue"),
                    0.5f,
                    new FrameModel(
                            Automobility.id("textures/entity/automobile/standard_blue.png"),
                            StandardFrameModel::new,
                            1f,
                            1f
                    )
            )
    );

    @Override
    public Identifier getId() {
        return this.id;
    }

    public static record FrameModel(
            Identifier texture,
            Function<EntityRendererFactory.Context, Model> model,
            float wheelSeparationLong,
            float wheelSeparationWide
    ) {}
}
