package io.github.foundationgames.automobility.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public enum EntityRenderHelper {;
    private static final List<Consumer<EntityRendererProvider.Context>> LISTENERS = new ArrayList<>();

    public static void registerContextListener(Consumer<EntityRendererProvider.Context> listener) {
        LISTENERS.add(listener);
    }

    public static void reload(EntityRendererProvider.Context ctx) {
        for (var l : LISTENERS) {
            l.accept(ctx);
        }
    }
}