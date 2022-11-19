package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.sound.AutomobilitySounds;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record AutomobileEngine(
        ResourceLocation id,
        float torque,
        float speed,
        Supplier<SoundEvent> sound,
        EngineModel model
) implements AutomobileComponent<AutomobileEngine> {
    public static final ResourceLocation ID = Automobility.rl("engine");
    public static final SimpleMapContentRegistry<AutomobileEngine> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileEngine EMPTY = REGISTRY.register(
            new AutomobileEngine(Automobility.rl("empty"), 0.01f, 0.01f,
                    () -> SoundEvents.MINECART_INSIDE,
                    new EngineModel(new ResourceLocation("empty"), Automobility.rl("empty"))
            )
    );

    public static final AutomobileEngine STONE = REGISTRY.register(
            new AutomobileEngine(Automobility.rl("stone"), 0.3f, 0.58f,
                    AutomobilitySounds.STONE_ENGINE::require,
                    new EngineModel(
                            Automobility.rl("textures/entity/automobile/engine/stone.png"), Automobility.rl("engine_stone"),
                            new AutomobileEngine.ExhaustPos(0, 7f, -8.3f, 50, 0)
                    )
            )
    );

    public static final AutomobileEngine IRON = REGISTRY.register(
            new AutomobileEngine(Automobility.rl("iron"), 0.5f, 0.75f,
                    AutomobilitySounds.IRON_ENGINE::require,
                    new EngineModel(
                            Automobility.rl("textures/entity/automobile/engine/iron.png"), Automobility.rl("engine_iron"),
                            new AutomobileEngine.ExhaustPos(-3.5f, 5.4f, -8, 26, 0),
                            new AutomobileEngine.ExhaustPos(3.5f, 5.4f, -8, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine COPPER = REGISTRY.register(
            new AutomobileEngine(Automobility.rl("copper"), 0.375f, 0.68f,
                    AutomobilitySounds.COPPER_ENGINE::require,
                    new EngineModel(
                            Automobility.rl("textures/entity/automobile/engine/copper.png"), Automobility.rl("engine_copper"),
                            new AutomobileEngine.ExhaustPos(2, 1.625f, -8.95f, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine GOLD = REGISTRY.register(
            new AutomobileEngine(Automobility.rl("gold"), 0.8f, 0.75f,
                    AutomobilitySounds.GOLD_ENGINE::require,
                    new EngineModel(
                            Automobility.rl("textures/entity/automobile/engine/gold.png"), Automobility.rl("engine_gold"),
                            new AutomobileEngine.ExhaustPos(4, 9.3f, -7.75f, 26, 0),
                            new AutomobileEngine.ExhaustPos(-4, 9.3f, -7.75f, 26, 0)
                    )
            )
    );

    public static final AutomobileEngine DIAMOND = REGISTRY.register(
            new AutomobileEngine(Automobility.rl("diamond"), 0.95f, 0.85f,
                    AutomobilitySounds.DIAMOND_ENGINE::require,
                    new EngineModel(
                            Automobility.rl("textures/entity/automobile/engine/diamond.png"), Automobility.rl("engine_diamond"),
                            new AutomobileEngine.ExhaustPos(3, 3.8f, -7.6f, 40, 0),
                            new AutomobileEngine.ExhaustPos(-3, 3.8f, -7.6f, 40, 0),
                            new AutomobileEngine.ExhaustPos(4, 7.075f, -4.95f, 40, 0),
                            new AutomobileEngine.ExhaustPos(-4, 7.075f, -4.95f, 40, 0)
                    )
            )
    );

    public static final AutomobileEngine CREATIVE = REGISTRY.register(
            new AutomobileEngine(Automobility.rl("creative"), 1f, 1f,
                    AutomobilitySounds.CREATIVE_ENGINE::require,
                    new EngineModel(
                            Automobility.rl("textures/entity/automobile/engine/creative.png"), Automobility.rl("engine_creative"),
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
    public ResourceLocation containerId() {
        return ID;
    }

    @Override
    public void forEachStat(Consumer<DisplayStat<AutomobileEngine>> action) {
        action.accept(STAT_TORQUE);
        action.accept(STAT_SPEED);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return "engine."+id.getNamespace()+"."+id.getPath();
    }

    public static record EngineModel(
            ResourceLocation texture,
            ResourceLocation modelId,
            ExhaustPos ... exhausts
    ) {}

    public static record ExhaustPos(
            float x, float y, float z,
            float pitch, float yaw
    ) {}
}
