package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Function;

public record AutomobileEngine(
        Identifier id,
        float torque,
        float speed,
        EngineModel model
) implements AutomobileComponent<AutomobileEngine> {
    public static final Identifier ID = Automobility.id("engine");
    public static final SimpleMapContentRegistry<AutomobileEngine> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileEngine EMPTY = REGISTRY.register(
            new AutomobileEngine(Automobility.id("empty"), 0.01f, 0.01f,
                    new EngineModel(new Identifier("empty"), Automobility.id("empty"))
            )
    );

    public static final AutomobileEngine STONE = REGISTRY.register(
            new AutomobileEngine(Automobility.id("stone"), 0.3f, 0.58f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/stone.png"), Automobility.id("engine_stone"),
                            new AutomobileEngine.ExhaustPos(0, 7f, -8.3f, 50, 0)
                    )
            )
    );

    public static final AutomobileEngine IRON = REGISTRY.register(
            new AutomobileEngine(Automobility.id("iron"), 0.5f, 0.75f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/iron.png"), Automobility.id("engine_iron"),
                            new AutomobileEngine.ExhaustPos(-3.5f, 5.4f, -8, 26, 0),
                            new AutomobileEngine.ExhaustPos(3.5f, 5.4f, -8, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine COPPER = REGISTRY.register(
            new AutomobileEngine(Automobility.id("copper"), 0.375f, 0.68f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/copper.png"), Automobility.id("engine_copper"),
                            new AutomobileEngine.ExhaustPos(2, 1.625f, -8.95f, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine GOLD = REGISTRY.register(
            new AutomobileEngine(Automobility.id("gold"), 0.8f, 0.75f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/gold.png"), Automobility.id("engine_gold"),
                            new AutomobileEngine.ExhaustPos(4, 9.3f, -7.75f, 26, 0),
                            new AutomobileEngine.ExhaustPos(-4, 9.3f, -7.75f, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine DIAMOND = REGISTRY.register(
            new AutomobileEngine(Automobility.id("diamond"), 0.95f, 0.85f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/diamond.png"), Automobility.id("engine_diamond"),
                            new AutomobileEngine.ExhaustPos(3, 3.8f, -7.6f, 40, 0),
                            new AutomobileEngine.ExhaustPos(-3, 3.8f, -7.6f, 40, 0),
                            new AutomobileEngine.ExhaustPos(4, 7.075f, -4.95f, 40, 0),
                            new AutomobileEngine.ExhaustPos(-4, 7.075f, -4.95f, 40, 0)
                    )
            )
    );

    public static final AutomobileEngine CREATIVE = REGISTRY.register(
            new AutomobileEngine(Automobility.id("creative"), 1f, 1f,
                    new EngineModel(
                            Automobility.id("textures/entity/automobile/engine/creative.png"), Automobility.id("engine_creative"),
                            new AutomobileEngine.ExhaustPos(0, 7, -7, 90, 0)
                    )
            )
    );

    public static final DisplayStat<AutomobileEngine> STAT_TORQUE = new DisplayStat<>("torque", AutomobileEngine::torque);
    public static final DisplayStat<AutomobileEngine> STAT_SPEED = new DisplayStat<>("speed", AutomobileEngine::speed);

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public Identifier containerId() {
        return ID;
    }

    @Override
    public void forEachStat(Consumer<DisplayStat<AutomobileEngine>> action) {
        action.accept(STAT_TORQUE);
        action.accept(STAT_SPEED);
    }

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
