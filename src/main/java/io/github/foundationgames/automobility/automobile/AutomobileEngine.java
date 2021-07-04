package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.EmptyModel;
import io.github.foundationgames.automobility.automobile.render.engine.CopperEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.CreativeEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.GoldEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.IronEngineModel;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record AutomobileEngine(
        Identifier id,
        float torque,
        float speed,
        EngineModel model
) implements SimpleMapContentRegistry.Identifiable {

    public static final SimpleMapContentRegistry<AutomobileEngine> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileEngine IRON = REGISTRY.register(
            new AutomobileEngine(Automobility.id("iron"), 0.5f, 1,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/iron.png"), Automobility.id("engine_iron"),
                            new AutomobileEngine.ExhaustPos(-3.5f, 5.4f, -8, 26, 0),
                            new AutomobileEngine.ExhaustPos(3.5f, 5.4f, -8, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine COPPER = REGISTRY.register(
            new AutomobileEngine(Automobility.id("copper"), 0.375f, 0.8f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/copper.png"), Automobility.id("engine_copper"),
                            new AutomobileEngine.ExhaustPos(2, 1.625f, -8.95f, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine GOLD = REGISTRY.register(
            new AutomobileEngine(Automobility.id("gold"), 0.7f, 0.75f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/gold.png"), Automobility.id("engine_gold"),
                            new AutomobileEngine.ExhaustPos(4, 9.3f, -7.75f, 26, 0),
                            new AutomobileEngine.ExhaustPos(-4, 9.3f, -7.75f, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine CREATIVE = REGISTRY.register(
            new AutomobileEngine(Automobility.id("creative"), 1f, 2f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/creative.png"), Automobility.id("engine_creative"),
                            new AutomobileEngine.ExhaustPos(0, 7, -7, 90, 0)
                    )
            )
    );

    @Override
    public Identifier getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return "engine."+id.getNamespace()+"."+id.getPath();
    }

    public static record EngineModel(
            Identifier texture,
            Identifier modelId,
            ExhaustPos ... exhausts
    ) {
        @Environment(EnvType.CLIENT)
        public Function<EntityRendererFactory.Context, Model> model() {
            return AutomobilityModels.MODELS.get(modelId);
        }
    }

    public static record ExhaustPos(
            float x, float y, float z,
            float pitch, float yaw
    ) {}
}
