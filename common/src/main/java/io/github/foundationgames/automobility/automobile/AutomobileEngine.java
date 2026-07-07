package io.github.foundationgames.automobility.automobile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.sound.AutomobilitySounds;
import io.github.foundationgames.automobility.util.DefaultRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record AutomobileEngine(
        boolean empty,
        float torque,
        float speed,
        Supplier<SoundEvent> sound,
        EngineModel model
) implements AutomobileComponent<AutomobileEngine> {
    public static final ResourceLocation ID = Automobility.rl("engine");

    public static final ResourceKey<Registry<AutomobileEngine>> REGISTRY = ResourceKey.createRegistryKey(Automobility.rl("automobile_engine"));
    public static final DefaultRegistrar<AutomobileEngine> BOOTSTRAP = new DefaultRegistrar<>(REGISTRY);

    public static final Codec<AutomobileEngine> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.optionalFieldOf("_empty", false).forGetter(AutomobileEngine::empty),
            Codec.FLOAT.fieldOf("torque").forGetter(AutomobileEngine::torque),
            Codec.FLOAT.fieldOf("speed").forGetter(AutomobileEngine::speed),
            SoundEvent.DIRECT_CODEC.fieldOf("sound").forGetter(e -> e.sound().get()),
            EngineModel.CODEC.fieldOf("display").forGetter(AutomobileEngine::model)
    ).apply(inst, AutomobileEngine::create));
    public static final Codec<ResourceKey<AutomobileEngine>> CODEC = ResourceKey.codec(REGISTRY);

    public static final StreamCodec<RegistryFriendlyByteBuf, AutomobileEngine> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AutomobileEngine::empty,
            ByteBufCodecs.FLOAT, AutomobileEngine::torque,
            ByteBufCodecs.FLOAT, AutomobileEngine::speed,
            SoundEvent.DIRECT_STREAM_CODEC, e -> e.sound().get(),
            EngineModel.STREAM_CODEC, AutomobileEngine::model,
            AutomobileEngine::create
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AutomobileEngine>> STREAM_CODEC = ByteBufCodecs.holder(REGISTRY, DIRECT_STREAM_CODEC);
    public static final EntityDataSerializer<Holder<AutomobileEngine>> SERIALIZER = EntityDataSerializer.forValueType(STREAM_CODEC);

    public static final AutomobileEngine EMPTY = new AutomobileEngine(true, 0.01f, 0.01f,
            () -> SoundEvents.MINECART_INSIDE,
            model(ResourceLocation.parse("empty"), Automobility.rl("empty"))
    );

    public static final ResourceKey<AutomobileEngine> EMPTY_KEY = BOOTSTRAP.register(Automobility.rl("empty"), EMPTY);

    public static AutomobileEngine of(float torque, float speed, Supplier<SoundEvent> sound, EngineModel model) {
        return new AutomobileEngine(false, torque, speed, sound, model);
    }

    public static AutomobileEngine create(boolean empty, float torque, float speed, SoundEvent sound, EngineModel model) {
        return new AutomobileEngine(empty, torque, speed, () -> sound, model);
    }

    public static final ResourceKey<AutomobileEngine> STONE = BOOTSTRAP.register(Automobility.rl("stone"), of(
            0.3f, 0.58f,
            AutomobilitySounds.STONE_ENGINE::require,
            model(
                    Automobility.rl("textures/entity/automobile/engine/stone.png"), Automobility.rl("engine/stone"),
                    new AutomobileEngine.ExhaustPos(0, 7f, -8.3f, 50, 0)
            )
    ));

    public static final ResourceKey<AutomobileEngine> IRON = BOOTSTRAP.register(Automobility.rl("iron"), of(
            0.5f, 0.75f,
            AutomobilitySounds.IRON_ENGINE::require,
            model(
                    Automobility.rl("textures/entity/automobile/engine/iron.png"), Automobility.rl("engine/iron"),
                    new AutomobileEngine.ExhaustPos(-3.5f, 5.4f, -8, 26, 0),
                    new AutomobileEngine.ExhaustPos(3.5f, 5.4f, -8, 26, 0)
            )
    ));

    public static final ResourceKey<AutomobileEngine> COPPER = BOOTSTRAP.register(Automobility.rl("copper"), of(
            0.375f, 0.68f,
            AutomobilitySounds.COPPER_ENGINE::require,
            model(
                    Automobility.rl("textures/entity/automobile/engine/copper.png"), Automobility.rl("engine/copper"),
                    new AutomobileEngine.ExhaustPos(2, 1.625f, -8.95f, 26, 0)
            )
    ));

    public static final ResourceKey<AutomobileEngine> GOLD = BOOTSTRAP.register(Automobility.rl("gold"), of(
            0.8f, 0.8f,
            AutomobilitySounds.GOLD_ENGINE::require,
            model(
                    Automobility.rl("textures/entity/automobile/engine/gold.png"), Automobility.rl("engine/gold"),
                    new AutomobileEngine.ExhaustPos(4, 9.3f, -7.75f, 26, 0),
                    new AutomobileEngine.ExhaustPos(-4, 9.3f, -7.75f, 26, 0)
            )
    ));

    public static final ResourceKey<AutomobileEngine> DIAMOND = BOOTSTRAP.register(Automobility.rl("diamond"), of(
            0.95f, 0.85f,
            AutomobilitySounds.DIAMOND_ENGINE::require,
            model(
                    Automobility.rl("textures/entity/automobile/engine/diamond.png"), Automobility.rl("engine/diamond"),
                    new AutomobileEngine.ExhaustPos(3, 3.8f, -7.6f, 40, 0),
                    new AutomobileEngine.ExhaustPos(-3, 3.8f, -7.6f, 40, 0),
                    new AutomobileEngine.ExhaustPos(4, 7.075f, -4.95f, 40, 0),
                    new AutomobileEngine.ExhaustPos(-4, 7.075f, -4.95f, 40, 0)
            )
    ));

    public static final ResourceKey<AutomobileEngine> CREATIVE = BOOTSTRAP.register(Automobility.rl("creative"), of(
            1f, 1f,
            AutomobilitySounds.CREATIVE_ENGINE::require,
            model(
                    Automobility.rl("textures/entity/automobile/engine/creative.png"), Automobility.rl("engine/creative"),
                    new AutomobileEngine.ExhaustPos(0, 7, -7, 90, 0)
            )
    ));

    public static final DisplayStat<AutomobileEngine> STAT_TORQUE = new DisplayStat<>("torque", AutomobileEngine::torque);
    public static final DisplayStat<AutomobileEngine> STAT_SPEED = new DisplayStat<>("speed", AutomobileEngine::speed);

    @Override
    public boolean isEmpty() {
        return empty();
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
        return Automobility.rl("invalid");
    }

    public static String getTranslationKey(ResourceLocation id) {
        return "engine."+id.getNamespace()+"."+id.getPath();
    }

    public static EngineModel model(ResourceLocation texture,
                                    ResourceLocation modelId,
                                    ExhaustPos... exhausts) {
        return new EngineModel(texture, modelId, List.of(exhausts));
    }

    public record EngineModel(
            ResourceLocation texture,
            ResourceLocation modelId,
            List<ExhaustPos> exhausts
    ) {
        public static final Codec<EngineModel> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(EngineModel::texture),
                ResourceLocation.CODEC.fieldOf("model").forGetter(EngineModel::modelId),
                Codec.list(ExhaustPos.CODEC).fieldOf("exhausts").forGetter(EngineModel::exhausts)
        ).apply(inst, EngineModel::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EngineModel> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, EngineModel::texture,
                ResourceLocation.STREAM_CODEC, EngineModel::modelId,
                ByteBufCodecs.<RegistryFriendlyByteBuf, ExhaustPos>list().apply(ExhaustPos.STREAM_CODEC), EngineModel::exhausts,
                EngineModel::new
        );
    }

    public record ExhaustPos(
            float x, float y, float z,
            float pitch, float yaw
    ) {
        public static final Codec<ExhaustPos> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ExtraCodecs.VECTOR3F.fieldOf("position").forGetter(ExhaustPos::pos),
                Codec.FLOAT.fieldOf("pitch").forGetter(ExhaustPos::pitch),
                Codec.FLOAT.fieldOf("yaw").forGetter(ExhaustPos::yaw)
        ).apply(inst, ExhaustPos::create));

        public static final StreamCodec<RegistryFriendlyByteBuf, ExhaustPos> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F, ExhaustPos::pos,
                ByteBufCodecs.FLOAT, ExhaustPos::pitch,
                ByteBufCodecs.FLOAT, ExhaustPos::yaw,
                ExhaustPos::create
        );

        public static ExhaustPos create(Vector3f pos, float pitch, float yaw) {
            return new ExhaustPos(pos.x(), pos.y(), pos.z(), pitch, yaw);
        }

        public Vector3f pos() {
            return new Vector3f(x(), y(), z());
        }
    }
}
