package io.github.foundationgames.automobility.automobile.attachment;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.automobile.DisplayStat;
import io.github.foundationgames.automobility.automobile.attachment.rear.BackhoeRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.BannerPostRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.BaseChestRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.EmptyRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.PassengerSeatRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.PaverRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.BlockRearAttachment;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public record RearAttachmentType<T extends RearAttachment>(
        Identifier id, BiFunction<RearAttachmentType<T>, AutomobileEntity, T> constructor, RearAttachmentModel model
) implements AutomobileComponent<RearAttachmentType<?>> {
    public static final Identifier ID = Automobility.id("rear_attachment");
    public static final SimpleMapContentRegistry<RearAttachmentType<?>> REGISTRY = new SimpleMapContentRegistry<>();

    public static final RearAttachmentType<EmptyRearAttachment> EMPTY = register(new RearAttachmentType<>(
            Automobility.id("empty"), EmptyRearAttachment::new, new RearAttachmentModel(new Identifier("empty"), Automobility.id("empty"), 0)
    ));

    public static final RearAttachmentType<PassengerSeatRearAttachment> PASSENGER_SEAT = register(new RearAttachmentType<>(
            Automobility.id("passenger_seat"), PassengerSeatRearAttachment::new,
            new RearAttachmentModel(Automobility.id("textures/entity/automobile/rear_attachment/passenger_seat.png"), Automobility.id("rearatt_passenger_seat"), 11)
    ));

    public static final RearAttachmentType<BlockRearAttachment> CRAFTING_TABLE = register(block("crafting_table", BlockRearAttachment::craftingTable));
    public static final RearAttachmentType<BlockRearAttachment> LOOM = register(block("loom", BlockRearAttachment::loom));
    public static final RearAttachmentType<BlockRearAttachment> CARTOGRAPHY_TABLE = register(block("cartography_table", BlockRearAttachment::cartographyTable));
    public static final RearAttachmentType<BlockRearAttachment> SMITHING_TABLE = register(block("smithing_table", BlockRearAttachment::smithingTable));
    public static final RearAttachmentType<BlockRearAttachment> GRINDSTONE = register(block("grindstone", Automobility.id("rearatt_grindstone"), BlockRearAttachment::grindstone));
    public static final RearAttachmentType<BlockRearAttachment> STONECUTTER = register(block("stonecutter", Automobility.id("rearatt_stonecutter"), BlockRearAttachment::stonecutter));
    public static final RearAttachmentType<BlockRearAttachment> AUTO_MECHANIC_TABLE = register(block("auto_mechanic_table", BlockRearAttachment::autoMechanicTable));

    public static final RearAttachmentType<BlockRearAttachment> CHEST = register(chest("chest", BaseChestRearAttachment::chest));
    public static final RearAttachmentType<BlockRearAttachment> ENDER_CHEST = register(chest("ender_chest", BaseChestRearAttachment::enderChest));
    public static final RearAttachmentType<BlockRearAttachment> SADDLED_BARREL = register(block("saddled_barrel", BaseChestRearAttachment::saddledBarrel));

    public static final RearAttachmentType<BannerPostRearAttachment> BANNER_POST = register(new RearAttachmentType<>(
            Automobility.id("banner_post"), BannerPostRearAttachment::new,
            new RearAttachmentModel(Automobility.id("textures/entity/automobile/rear_attachment/banner_post.png"), Automobility.id("rearatt_banner_post"), 10)
    ));

    public static final RearAttachmentType<BackhoeRearAttachment> BACKHOE = register(new RearAttachmentType<>(
            Automobility.id("backhoe"), BackhoeRearAttachment::new,
            new RearAttachmentModel(Automobility.id("textures/entity/automobile/rear_attachment/backhoe.png"), Automobility.id("rearatt_plow"), 11)
    ));

    public static final RearAttachmentType<PaverRearAttachment> PAVER = register(new RearAttachmentType<>(
            Automobility.id("paver"), PaverRearAttachment::new,
            new RearAttachmentModel(Automobility.id("textures/entity/automobile/rear_attachment/paver.png"), Automobility.id("rearatt_plow"), 11)
    ));

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public Identifier containerId() {
        return ID;
    }

    @Override
    public void forEachStat(Consumer<DisplayStat<RearAttachmentType<?>>> action) {
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    private static RearAttachmentType<BlockRearAttachment> chest(String name, BiFunction<RearAttachmentType<BlockRearAttachment>, AutomobileEntity, BlockRearAttachment> constructor) {
        return block(name, Automobility.id("rearatt_chest"), constructor);
    }

    private static RearAttachmentType<BlockRearAttachment> block(String name, BiFunction<RearAttachmentType<BlockRearAttachment>, AutomobileEntity, BlockRearAttachment> constructor) {
        return block(name, Automobility.id("rearatt_block"), constructor);
    }

    private static RearAttachmentType<BlockRearAttachment> block(String name, Identifier model, BiFunction<RearAttachmentType<BlockRearAttachment>, AutomobileEntity, BlockRearAttachment> constructor) {
        return new RearAttachmentType<>(
                Automobility.id(name), constructor,
                new RearAttachmentModel(Automobility.id("textures/entity/automobile/rear_attachment/"+name+".png"), model, 11)
        );
    }

    private static <T extends RearAttachment> RearAttachmentType<T> register(RearAttachmentType<T> entry) {
        REGISTRY.register(entry);
        return entry;
    }

    public record RearAttachmentModel(Identifier texture, Identifier modelId, float pivotDistPx) {
        
        public Function<EntityRendererFactory.Context, Model> model() {
            return AutomobilityModels.MODELS.get(modelId);
        }
    }
}
