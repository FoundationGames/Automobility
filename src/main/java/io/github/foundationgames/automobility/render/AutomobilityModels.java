package io.github.foundationgames.automobility.render;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.render.EmptyModel;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public enum AutomobilityModels {;
    public static final Map<Identifier, Function<EntityRendererFactory.Context, Model>> MODELS = new HashMap<>();

    
    public static void init() {
        MODELS.put(Automobility.id("empty"), EmptyModel::new);

        MODELS.put(Automobility.id("frame_standard"), StandardFrameModel::new);
        MODELS.put(Automobility.id("frame_tractor"), TractorFrameModel::new);
        MODELS.put(Automobility.id("frame_shopping_cart"), ShoppingCartFrameModel::new);
        MODELS.put(Automobility.id("frame_c_arr"), CARRFrameModel::new);
        MODELS.put(Automobility.id("frame_pineapple"), PineappleFrameModel::new);
        MODELS.put(Automobility.id("frame_motorcar"), MotorcarFrameModel::new);
        MODELS.put(Automobility.id("frame_rickshaw"), RickshawFrameModel::new);
        MODELS.put(Automobility.id("frame_dababy"), DaBabyFrameModel::new);

        MODELS.put(Automobility.id("wheel_standard"), StandardWheelModel::new);
        MODELS.put(Automobility.id("wheel_off_road"), OffRoadWheelModel::new);
        MODELS.put(Automobility.id("wheel_steel"), SteelWheelModel::new);
        MODELS.put(Automobility.id("wheel_tractor"), TractorWheelModel::new);
        MODELS.put(Automobility.id("wheel_carriage"), CarriageWheelModel::new);
        MODELS.put(Automobility.id("wheel_convertible"), ConvertibleWheelModel::new);

        MODELS.put(Automobility.id("engine_stone"), StoneEngineModel::new);
        MODELS.put(Automobility.id("engine_iron"), IronEngineModel::new);
        MODELS.put(Automobility.id("engine_copper"), CopperEngineModel::new);
        MODELS.put(Automobility.id("engine_gold"), GoldEngineModel::new);
        MODELS.put(Automobility.id("engine_diamond"), DiamondEngineModel::new);
        MODELS.put(Automobility.id("engine_creative"), CreativeEngineModel::new);

        MODELS.put(Automobility.id("rearatt_passenger_seat"), PassengerSeatRearAttachmentModel::new);
        MODELS.put(Automobility.id("rearatt_block"), BlockRearAttachmentModel::new);
        MODELS.put(Automobility.id("rearatt_grindstone"), GrindstoneRearAttachmentModel::new);
        MODELS.put(Automobility.id("rearatt_stonecutter"), StonecutterRearAttachmentModel::new);
        MODELS.put(Automobility.id("rearatt_chest"), ChestRearAttachmentModel::new);
        MODELS.put(Automobility.id("rearatt_banner_post"), BannerPostRearAttachmentModel::new);
        MODELS.put(Automobility.id("rearatt_plow"), PlowRearAttachmentModel::new);

        MODELS.put(Automobility.id("frontatt_mob_controller"), MobControllerFrontAttachmentModel::new);
        MODELS.put(Automobility.id("frontatt_harvester"), HarvesterFrontAttachmentModel::new);
    }
}
