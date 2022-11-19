package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.attachment.front.HarvesterFrontAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.front.MobControllerFrontAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.BannerPostRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.BlockRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.ChestRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.GrindstoneRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.PassengerSeatRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.PlowRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.attachment.rear.StonecutterRearAttachmentModel;
import io.github.foundationgames.automobility.automobile.render.engine.CopperEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.CreativeEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.DiamondEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.GoldEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.IronEngineModel;
import io.github.foundationgames.automobility.automobile.render.engine.StoneEngineModel;
import io.github.foundationgames.automobility.automobile.render.frame.CARRFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.DaBabyFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.MotorcarFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.PineappleFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.RickshawFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.ShoppingCartFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.StandardFrameModel;
import io.github.foundationgames.automobility.automobile.render.frame.TractorFrameModel;
import io.github.foundationgames.automobility.automobile.render.wheel.CarriageWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.ConvertibleWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.OffRoadWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.StandardWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.SteelWheelModel;
import io.github.foundationgames.automobility.automobile.render.wheel.TractorWheelModel;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum AutomobileModels {;
    private static final ResourceLocation EMPTY = Automobility.rl("empty");

    public static final ResourceLocation SKID_EFFECT = Automobility.rl("misc_skideffect");
    public static final ResourceLocation EXHAUST_FUMES = Automobility.rl("misc_exhaustfumes");

    private static final Map<ResourceLocation, Function<EntityRendererProvider.Context, Model>> modelProviders = new HashMap<>();
    private static final Map<ResourceLocation, Model> models = new HashMap<>();
    
    public static void register(ResourceLocation location, Function<EntityRendererProvider.Context, Model> model) {
        modelProviders.put(location, model);
    }

    public static void init() {
        EntityRenderHelper.registerContextListener(ctx -> {
            models.clear();
            models.put(EMPTY, new EmptyModel());

            for (var entry : modelProviders.entrySet()) {
                models.put(entry.getKey(), entry.getValue().apply(ctx));
            }
        });

        register(Automobility.rl("frame_standard"), StandardFrameModel::new);
        register(Automobility.rl("frame_tractor"), TractorFrameModel::new);
        register(Automobility.rl("frame_shopping_cart"), ShoppingCartFrameModel::new);
        register(Automobility.rl("frame_c_arr"), CARRFrameModel::new);
        register(Automobility.rl("frame_pineapple"), PineappleFrameModel::new);
        register(Automobility.rl("frame_motorcar"), MotorcarFrameModel::new);
        register(Automobility.rl("frame_rickshaw"), RickshawFrameModel::new);
        register(Automobility.rl("frame_dababy"), DaBabyFrameModel::new);

        register(Automobility.rl("wheel_standard"), StandardWheelModel::new);
        register(Automobility.rl("wheel_off_road"), OffRoadWheelModel::new);
        register(Automobility.rl("wheel_steel"), SteelWheelModel::new);
        register(Automobility.rl("wheel_tractor"), TractorWheelModel::new);
        register(Automobility.rl("wheel_carriage"), CarriageWheelModel::new);
        register(Automobility.rl("wheel_convertible"), ConvertibleWheelModel::new);

        register(Automobility.rl("engine_stone"), StoneEngineModel::new);
        register(Automobility.rl("engine_iron"), IronEngineModel::new);
        register(Automobility.rl("engine_copper"), CopperEngineModel::new);
        register(Automobility.rl("engine_gold"), GoldEngineModel::new);
        register(Automobility.rl("engine_diamond"), DiamondEngineModel::new);
        register(Automobility.rl("engine_creative"), CreativeEngineModel::new);

        register(Automobility.rl("rearatt_passenger_seat"), PassengerSeatRearAttachmentModel::new);
        register(Automobility.rl("rearatt_block"), BlockRearAttachmentModel::new);
        register(Automobility.rl("rearatt_grindstone"), GrindstoneRearAttachmentModel::new);
        register(Automobility.rl("rearatt_stonecutter"), StonecutterRearAttachmentModel::new);
        register(Automobility.rl("rearatt_chest"), ChestRearAttachmentModel::new);
        register(Automobility.rl("rearatt_banner_post"), BannerPostRearAttachmentModel::new);
        register(Automobility.rl("rearatt_plow"), PlowRearAttachmentModel::new);

        register(Automobility.rl("frontatt_mob_controller"), MobControllerFrontAttachmentModel::new);
        register(Automobility.rl("frontatt_harvester"), HarvesterFrontAttachmentModel::new);

        register(SKID_EFFECT, SkidEffectModel::new);
        register(EXHAUST_FUMES, ExhaustFumesModel::new);
    }

    public static Model getModelOrNull(ResourceLocation location) {
        return models.get(location);
    }

    public static Model getModel(ResourceLocation location) {
        var result = getModelOrNull(location);
        if (result == null) {
            return getEmpty();
        }
        return result;
    }

    public static Model getEmpty() {
        return getModelOrNull(EMPTY);
    }
}
