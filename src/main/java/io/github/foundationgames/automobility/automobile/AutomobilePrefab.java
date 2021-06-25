package io.github.foundationgames.automobility.automobile;

import net.minecraft.util.Identifier;

public record AutomobilePrefab(Identifier id, AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {}
