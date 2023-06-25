package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.util.Eventual;
import io.github.foundationgames.automobility.util.RegistryQueue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public enum AutomobilityEntities {;
    public static final Eventual<EntityType<AutomobileEntity>> AUTOMOBILE = RegistryQueue.register(BuiltInRegistries.ENTITY_TYPE,
            Automobility.rl("automobile"),
            () -> Platform.get().entityType(MobCategory.MISC, AutomobileEntity::new, new EntityDimensions(1f, 0.66f, true), 3, 10)
    );

    public static final TagKey<EntityType<?>> DASH_PANEL_BOOSTABLES = TagKey.create(Registries.ENTITY_TYPE, Automobility.rl("dash_panel_boostables"));

    public static final DamageSource AUTOMOBILE_DAMAGE_SOURCE = new AutomobileDamageSource("automobile");

    public static void init() {
    }
}
