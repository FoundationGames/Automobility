package io.github.foundationgames.automobility.util;

import net.minecraft.client.render.entity.EntityRendererFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum EntityRenderHelper {;
    private static final List<Consumer<EntityRendererFactory.Context>> LISTENERS = new ArrayList<>();

    public static void registerContextListener(Consumer<EntityRendererFactory.Context> listener) {
        LISTENERS.add(listener);
    }

    public static void reload(EntityRendererFactory.Context ctx) {
        for (var l : LISTENERS) {
            l.accept(ctx);
        }
    }
}