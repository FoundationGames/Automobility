package io.github.foundationgames.automobility.automobile;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record AutomobileFrame(
        Model model,
        Identifier texture,
        float weight
) {
    public static final SimpleMapContentRegistry<AutomobileFrame> REGISTRY = new SimpleMapContentRegistry<>();

    public static final AutomobileFrame STANDARD_BLUE = REGISTRY.register(
            Automobility.id("standard_blue"),
            new AutomobileFrame(new Model(), Automobility.id("textures/entity/automobile/standard_blue.png"), 0.5f)
    );

    public static record Model(

    ) {

    }
}
