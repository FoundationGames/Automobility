package io.github.foundationgames.automobility.render;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.engine.CopperEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.CreativeEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.GoldEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.IronEngineModel;
import io.github.foundationgames.automobility.automobile.render.frame.*;
import io.github.foundationgames.automobility.automobile.render.wheel.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public enum AutomobilityModels {;
    public static final Map<Identifier, Function<EntityRendererFactory.Context, Model>> MODELS = new HashMap<>();

    @Environment(EnvType.CLIENT)
    public static void init() {
        MODELS.put(Automobility.id("frame_standard"), StandardFrameModel::new);
        MODELS.put(Automobility.id("frame_tractor"), TractorFrameModel::new);
        MODELS.put(Automobility.id("frame_shopping_cart"), ShoppingCartFrameModel::new);
        MODELS.put(Automobility.id("frame_c_arr"), CARRFrameModel::new);
        MODELS.put(Automobility.id("frame_pineapple"), PineappleFrameModel::new);
        MODELS.put(Automobility.id("frame_dababy"), DaBabyFrameModel::new);

        MODELS.put(Automobility.id("wheel_standard"), StandardWheelModel::new);
        MODELS.put(Automobility.id("wheel_off_road"), OffRoadWheelModel::new);
        MODELS.put(Automobility.id("wheel_steel"), SteelWheelModel::new);
        MODELS.put(Automobility.id("wheel_tractor"), TractorWheelModel::new);
        MODELS.put(Automobility.id("wheel_convertible"), ConvertibleWheelModel::new);

        MODELS.put(Automobility.id("engine_iron"), IronEngineModel::new);
        MODELS.put(Automobility.id("engine_copper"), CopperEngineModel::new);
        MODELS.put(Automobility.id("engine_gold"), GoldEngineModel::new);
        MODELS.put(Automobility.id("engine_creative"), CreativeEngineModel::new);
    }
}
