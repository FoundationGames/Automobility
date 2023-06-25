package io.github.foundationgames.automobility.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class AutomobileDamageSource extends DamageSource {
    protected AutomobileDamageSource(String name) {
        super(new Holder.Direct<>(new DamageType(name, 0.0F)));
    }
}
