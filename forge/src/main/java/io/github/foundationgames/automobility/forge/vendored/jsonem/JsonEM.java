package io.github.foundationgames.automobility.forge.vendored.jsonem;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonEM {
    public static final Logger LOG = LogManager.getLogger("Automobility Vendored | Json Entity Models");

    public static void registerModelLayer(ModelLayerLocation layer) {
        ForgeHooksClient.registerLayerDefinition(layer, () -> LayerDefinition.create(new MeshDefinition(), 32, 32));
    }
}
