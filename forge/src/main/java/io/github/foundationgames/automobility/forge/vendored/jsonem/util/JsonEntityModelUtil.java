package io.github.foundationgames.automobility.forge.vendored.jsonem.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.github.foundationgames.automobility.forge.vendored.jsonem.JsonEM;
import io.github.foundationgames.automobility.forge.vendored.jsonem.serialization.JsonEMCodecs;
import io.github.foundationgames.automobility.util.InitlessConstants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.ForgeHooksClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

public final class JsonEntityModelUtil {
    public static final Gson GSON = new Gson();

    private JsonEntityModelUtil() {}

    public static Optional<LayerDefinition> readJson(InputStream data) {
        JsonElement json = GSON.fromJson(GSON.newJsonReader(new InputStreamReader(data)), JsonObject.class);

        return JsonEMCodecs.LAYER_DEFINITION.decode(JsonOps.INSTANCE, json).result().map(Pair::getFirst);
    }

    public static void loadModels(ResourceManager manager, Map<ModelLayerLocation, LayerDefinition> models) {
        var tempMap = new ImmutableMap.Builder<ModelLayerLocation, LayerDefinition>();
        ForgeHooksClient.loadLayerDefinitions(tempMap);

        // Only load Automobility models, don't mess with other mods (or the vanilla game)
        var layers = Streams.concat(
                ModelLayers.getKnownLocations(),
                tempMap.build().keySet().stream()
                        .filter(ml -> InitlessConstants.AUTOMOBILITY.equals(ml.getModel().getNamespace()))
        );
        layers.forEach(layer -> {
            var modelLoc = new ResourceLocation(layer.getModel().getNamespace(), "models/entity/"+layer.getModel().getPath()+"/"+layer.getLayer()+".json");

            var res = manager.getResource(modelLoc);

            if (res.isPresent()) {
                try {
                    try (var in = res.get().open()) {
                        var data = JsonEntityModelUtil.readJson(in);
                        data.ifPresent(model -> models.put(layer, model));
                    }
                } catch (IOException e) {
                    JsonEM.LOG.error(e);
                }
            }
        });
    }
}
